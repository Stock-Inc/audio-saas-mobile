package dev.alnoer.audiosaas.feature.upload

import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class UploadFile(
    val name: String,
    val contentType: String,
    val size: Long?,
    val openStream: () -> InputStream,
)

interface UploadFileReader {
    fun read(uri: String): UploadFile
}

interface FileUploadRepository {
    suspend fun upload(file: UploadFile)
}

class UploadFileUseCase @Inject constructor(
    private val fileReader: UploadFileReader,
    private val repository: FileUploadRepository,
) {
    suspend operator fun invoke(uri: String) = withContext(Dispatchers.IO) {
        repository.upload(fileReader.read(uri))
    }
}
