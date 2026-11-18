package dev.alnoer.audiosaas.library

data class Folder(val id: String, val name: String, val parentId: String?)
data class Tag(val id: String, val name: String)
data class AudioFile(
    val id: String,
    val name: String,
    val status: String,
    val folder: Folder?,
    val tags: List<Tag>,
    val sizeBytes: Long,
    val createdAt: String,
    val errorMessage: String?,
)

enum class AudioSort(val label: String) {
    NEWEST("Сначала новые"), OLDEST("Сначала старые"),
    NAME_ASC("Имя: А–Я"), NAME_DESC("Имя: Я–А"),
    SIZE_ASC("Размер: по возрастанию"), SIZE_DESC("Размер: по убыванию"),
}

interface LibraryRepository {
    suspend fun folders(): List<Folder>
    suspend fun children(parentId: String?): List<Folder>
    suspend fun tags(): List<Tag>
    suspend fun saveFolder(id: String?, name: String, parentId: String?): Folder
    suspend fun saveTag(id: String?, name: String): Tag
    suspend fun deleteFolder(id: String)
    suspend fun deleteTag(id: String)
    // null selects all files; "null" selects files without a folder.
    suspend fun audio(folderId: String? = null, tagId: String? = null, sort: AudioSort = AudioSort.NEWEST): List<AudioFile>
    suspend fun audioFile(id: String): AudioFile
    suspend fun move(id: String, folderId: String?): AudioFile
    suspend fun moveMany(ids: Set<String>, folderId: String?): List<AudioFile>
    suspend fun assignTags(id: String, tagIds: Set<String>): AudioFile
}

fun validatedName(name: String): String = name.trim().also {
    require(it.length in 1..100) { "Имя должно содержать от 1 до 100 символов." }
}

class LibraryException(val status: Int, message: String) : Exception(message)
