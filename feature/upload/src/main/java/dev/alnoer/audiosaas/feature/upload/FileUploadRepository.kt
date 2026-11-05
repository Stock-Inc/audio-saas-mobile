package dev.alnoer.audiosaas.feature.upload

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.alnoer.audiosaas.feature.auth.domain.repository.LocalAuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.streams.asInput
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class FileUploadRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val httpClient: HttpClient,
    private val localAuthRepository: LocalAuthRepository,
) {
    private val contentResolver = context.contentResolver

    suspend fun upload(uri: Uri) {
        val uploadUrl = resolveUploadUrl(BuildConfig.UPLOAD_URL)
        val tokens = localAuthRepository.tokens.first()
        check(tokens?.isAccessTokenValid() == true) {
            "Сессия истекла. Войдите снова."
        }
        val (fileName, fileSize) = contentResolver.readMetadata(uri)
        val headers = Headers.build {
            append(HttpHeaders.ContentDisposition, "filename=\"${safeUploadFileName(fileName)}\"")
            append(
                HttpHeaders.ContentType,
                contentResolver.getType(uri) ?: "application/octet-stream",
            )
        }
        val response = httpClient.post(uploadUrl) {
            bearerAuth(tokens.accessToken)
            accept(ContentType.Application.Json)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("requestId", UUID.randomUUID().toString())
                        append("recordId", UUID.randomUUID().toString())
                        append(
                            "options",
                            "{}",
                            Headers.build {
                                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                            },
                        )
                        appendInput("file", headers, fileSize) {
                            checkNotNull(contentResolver.openInputStream(uri)) {
                                "Не удалось открыть выбранный файл."
                            }.asInput()
                        }
                    },
                ),
            )
        }

        Log.d("UPLOAD", "Status: ${response.status}")
        Log.d("UPLOAD", "Location: ${response.headers[HttpHeaders.Location]}")
        check(response.status.value == 202) {
            uploadFailureMessage(response.status.value)
        }
    }
}

private data class FileMetadata(
    val name: String,
    val size: Long?,
)

private fun ContentResolver.readMetadata(uri: Uri): FileMetadata {
    var name = uri.lastPathSegment ?: "audio"
    var size: Long? = null

    query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
        null,
        null,
        null,
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if ((nameIndex >= 0) && !cursor.isNull(nameIndex)) {
                name = cursor.getString(nameIndex)
            }
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if ((sizeIndex >= 0) && !cursor.isNull(sizeIndex)) {
                size = cursor.getLong(sizeIndex)
            }
        }
    }

    return FileMetadata(name = name, size = size)
}

private const val AUDIO_UPLOAD_PATH = "/api/v1/audio"

internal fun resolveUploadUrl(url: String): String {
    require(url.startsWith("https://") || url.startsWith("http://")) {
        "Адрес загрузки не настроен."
    }
    val normalizedUrl = url.trimEnd('/')
    return if (normalizedUrl.endsWith(AUDIO_UPLOAD_PATH)) {
        normalizedUrl
    } else {
        normalizedUrl + AUDIO_UPLOAD_PATH
    }
}

internal fun uploadFailureMessage(statusCode: Int): String = when (statusCode) {
    302 -> "Сервер перенаправил запрос на страницу входа."
    400 -> "Файл или параметры загрузки некорректны."
    401 -> "Не удалось авторизовать загрузку. Войдите снова."
    413 -> "Файл превышает допустимый размер."
    415 -> "Сервер не поддерживает тип выбранного файла."
    502 -> "Сервис обработки аудио временно недоступен."
    else -> "Сервер отклонил загрузку ($statusCode)."
}

internal fun safeUploadFileName(name: String): String =
    name.replace(Regex("""[\r\n"]"""), "_")
