package dev.alnoer.audiosaas.feature.upload.data

import dev.alnoer.audiosaas.auth.repository.AccessTokenProvider
import dev.alnoer.audiosaas.feature.upload.BuildConfig
import dev.alnoer.audiosaas.feature.upload.FileUploadRepository
import dev.alnoer.audiosaas.feature.upload.UploadFile
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

@Singleton
class KtorFileUploadRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val accessTokenProvider: AccessTokenProvider,
) : FileUploadRepository {

    override suspend fun upload(file: UploadFile) {
        val accessToken = checkNotNull(accessTokenProvider.validAccessToken()) {
            "Сессия истекла. Войдите снова."
        }
        val response = httpClient.post(resolveUploadUrl(BuildConfig.UPLOAD_URL)) {
            bearerAuth(accessToken)
            accept(ContentType.Application.Json)
            setBody(file.asMultipartContent())
        }

        check(response.status.value == 202) {
            uploadFailureMessage(response.status.value)
        }
    }
}

private fun UploadFile.asMultipartContent() = MultiPartFormDataContent(
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
        appendInput(
            key = "file",
            headers = Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"${safeUploadFileName(name)}\"")
                append(HttpHeaders.ContentType, contentType)
            },
            size = size,
        ) {
            openStream().asInput()
        }
    },
)

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
