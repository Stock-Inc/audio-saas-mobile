package dev.alnoer.audiosaas.feature.upload

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.utils.io.streams.asInput
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileUploadRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val httpClient: HttpClient,
) {
    private val contentResolver = context.contentResolver

    suspend fun upload(uri: Uri) {
        val uploadUrl = requireConfiguredUploadUrl(BuildConfig.UPLOAD_URL)
        val (fileName, fileSize) = contentResolver.readMetadata(uri)
        val headers = Headers.build {
            append(HttpHeaders.ContentDisposition, "filename=\"${safeUploadFileName(fileName)}\"")
            append(
                HttpHeaders.ContentType,
                contentResolver.getType(uri) ?: "application/octet-stream",
            )
        }
        val response = httpClient.post(uploadUrl) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        appendInput("file", headers, fileSize) {
                            checkNotNull(contentResolver.openInputStream(uri)) {
                                "Не удалось открыть выбранный файл."
                            }.asInput()
                        }
                    },
                ),
            )
        }

        check(response.status.isSuccess()) {
            "Сервер отклонил загрузку (${response.status.value})."
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

internal fun requireConfiguredUploadUrl(url: String): String {
    require(url.startsWith("https://") || url.startsWith("http://")) {
        "Адрес загрузки не настроен."
    }
    return url
}

internal fun safeUploadFileName(name: String): String =
    name.replace(Regex("""[\r\n"]"""), "_")
