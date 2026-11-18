package dev.alnoer.audiosaas.library

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.alnoer.audiosaas.auth.repository.AccessTokenProvider
import dev.alnoer.audiosaas.auth.repository.AuthSessionRepository
import dev.alnoer.audiosaas.core.library.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

class KtorLibraryRepository @Inject constructor(
    private val client: HttpClient,
    private val tokens: AccessTokenProvider,
    private val session: AuthSessionRepository,
) : LibraryRepository {
    internal var baseUrl = BuildConfig.API_BASE_URL

    private suspend fun request(
        path: String,
        method: HttpMethod = HttpMethod.Get,
        body: JsonObject? = null,
        query: Map<String, String?> = emptyMap(),
        success: Int = 200,
    ): JsonElement = withContext(Dispatchers.IO) {
        val token = tokens.validAccessToken()
        if (token == null) {
            session.clearTokens()
            throw LibraryException(401, "Сессия истекла. Войдите снова.")
        }
        val response = client.request(baseUrl.trimEnd('/') + "/api/v1/" + path) {
            this.method = method
            bearerAuth(token)
            accept(ContentType.Application.Json)
            url { query.forEach { (key, value) -> if (value != null) parameters.append(key, value) } }
            if (body != null) {
                contentType(ContentType.Application.Json)
                setBody(body.toString())
            }
        }
        if (response.status.value != success) {
            if (response.status.value == 401) session.clearTokens()
            throw LibraryException(response.status.value, when (response.status.value) {
                400 -> "Некорректные данные запроса."
                401 -> "Сессия истекла. Войдите снова."
                404 -> "Объект удалён или недоступен."
                409 -> if (method == HttpMethod.Delete && path.startsWith("folders/"))
                    "Сначала удалите подпапки. Записи удалены не будут."
                    else "Такое имя уже существует."
                else -> "Ошибка сервера (${response.status.value}). Попробуйте ещё раз."
            })
        }
        if (success == 204) JsonNull else Json.parseToJsonElement(response.bodyAsText())
    }

    override suspend fun folders() = request("folders").jsonArray.map(::folder)
    override suspend fun children(parentId: String?) = request("folders/${parentId ?: "null"}/children").jsonArray.map(::folder)
    override suspend fun tags() = request("tags").jsonArray.map(::tag)
    override suspend fun saveFolder(id: String?, name: String, parentId: String?): Folder = folder(
        request(if (id == null) "folders" else "folders/$id",
            if (id == null) HttpMethod.Post else HttpMethod.Patch,
            buildJsonObject {
                put("name", validatedName(name))
                if (id == null) put("parentId", parentId?.let(::JsonPrimitive) ?: JsonNull)
            }, success = if (id == null) 201 else 200),
    )
    override suspend fun saveTag(id: String?, name: String): Tag = tag(
        request(if (id == null) "tags" else "tags/$id",
            if (id == null) HttpMethod.Post else HttpMethod.Patch,
            buildJsonObject { put("name", validatedName(name)) }, success = if (id == null) 201 else 200),
    )
    override suspend fun deleteFolder(id: String) { request("folders/$id", HttpMethod.Delete, success = 204) }
    override suspend fun deleteTag(id: String) { request("tags/$id", HttpMethod.Delete, success = 204) }
    override suspend fun audio(folderId: String?, tagId: String?, sort: AudioSort) =
        request("audio", query = mapOf("folderId" to folderId, "tagId" to tagId, "sort" to sort.name)).jsonArray.map(::audio)
    override suspend fun audioFile(id: String) = audio(request("audio/$id"))
    override suspend fun move(id: String, folderId: String?) = audio(request("audio/$id/folder", HttpMethod.Put,
        buildJsonObject { put("folderId", folderId?.let(::JsonPrimitive) ?: JsonNull) }))
    override suspend fun moveMany(ids: Set<String>, folderId: String?): List<AudioFile> {
        require(ids.isNotEmpty())
        return request("audio/folder", HttpMethod.Put, buildJsonObject {
            put("audioIds", JsonArray(ids.map(::JsonPrimitive)))
            put("folderId", folderId?.let(::JsonPrimitive) ?: JsonNull)
        }).jsonArray.map(::audio)
    }
    override suspend fun assignTags(id: String, tagIds: Set<String>) = audio(request("audio/$id/tags", HttpMethod.Put,
        buildJsonObject { put("tagIds", JsonArray(tagIds.map(::JsonPrimitive))) }))

    private fun folder(value: JsonElement): Folder = value.jsonObject.let {
        Folder(it.getValue("id").jsonPrimitive.content, it.getValue("name").jsonPrimitive.content, it["parentId"]?.jsonPrimitive?.contentOrNull)
    }
    private fun tag(value: JsonElement): Tag = value.jsonObject.let {
        Tag(it.getValue("id").jsonPrimitive.content, it.getValue("name").jsonPrimitive.content)
    }
    private fun audio(value: JsonElement): AudioFile = value.jsonObject.let {
        AudioFile(
            it.getValue("id").jsonPrimitive.content,
            it.getValue("originalFilename").jsonPrimitive.content,
            it.getValue("status").jsonPrimitive.content,
            it["folder"]?.takeUnless { item -> item == JsonNull }?.let(::folder),
            it.getValue("tags").jsonArray.map(::tag),
            it.getValue("sizeBytes").jsonPrimitive.long,
            it.getValue("createdAt").jsonPrimitive.content,
            it["errorMessage"]?.jsonPrimitive?.contentOrNull,
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LibraryModule {
    @Binds
    abstract fun repository(implementation: KtorLibraryRepository): LibraryRepository
}
