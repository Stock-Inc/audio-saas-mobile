package dev.alnoer.audiosaas.feature.upload.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.alnoer.audiosaas.feature.upload.UploadFile
import dev.alnoer.audiosaas.feature.upload.UploadFileReader
import javax.inject.Inject
import androidx.core.net.toUri

class ContentResolverUploadFileReader @Inject constructor(
    @ApplicationContext context: Context,
) : UploadFileReader {
    private val contentResolver = context.contentResolver

    override fun read(uri: String): UploadFile {
        val documentUri = uri.toUri()
        val (name, size) = contentResolver.readMetadata(documentUri)

        return UploadFile(
            name = name,
            contentType = contentResolver.getType(documentUri) ?: "application/octet-stream",
            size = size,
            openStream = {
                checkNotNull(contentResolver.openInputStream(documentUri)) {
                    "Не удалось открыть выбранный файл."
                }
            },
        )
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
