package dev.alnoer.audiosaas.feature.folders

import androidx.lifecycle.SavedStateHandle
import dev.alnoer.audiosaas.library.*
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

class FoldersViewModelTest {
    @Test
    fun nestedCreationAndFailedDelete() {
        val repository = FakeLibrary()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        try {
            val model = FoldersViewModel(repository, SavedStateHandle()).also { it.workScope = scope }
            model.open("parent")
            assertEquals(listOf("child"), model.state.value.children.map { it.id })
            assertEquals("parent", repository.requestedFolder)
            model.save(false, null, "New")
            assertEquals("parent", repository.createdParent)
            repository.failure = LibraryException(409, "Has children")
            model.delete(false, "parent")
            assertEquals("Has children", model.state.value.editError)
            assertEquals(1, model.state.value.revision)
            assertEquals("parent", model.parentId)
        } finally { scope.cancel() }
    }

    @Test
    fun deletedOpenFolderReturnsToRoot() {
        val repository = FakeLibrary()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        try {
            val model = FoldersViewModel(repository, SavedStateHandle()).also { it.workScope = scope }
            model.open("missing")
            assertNull(model.parentId)
            assertEquals("null", repository.requestedFolder)
            assertFalse(model.state.value.loading)
            assertNull(model.state.value.error)
        } finally { scope.cancel() }
    }
}

private open class FakeLibrary : LibraryRepository {
    var folderList = listOf(Folder("parent", "Parent", null), Folder("child", "Child", "parent"))
    var tagList = listOf(Tag("tag", "Important"))
    val file = AudioFile("audio", "Meeting", "COMPLETED", null, emptyList(), 42, "today", null)
    var files = listOf(file)
    var requestedFolder: String? = null
    var createdParent: String? = null
    var failure: Exception? = null
    var gate: CompletableDeferred<Unit>? = null
    override suspend fun folders() = folderList
    override suspend fun children(parentId: String?) = folderList.filter { it.parentId == parentId }
    override suspend fun tags() = tagList
    override suspend fun audio(folderId: String?, tagId: String?, sort: AudioSort): List<AudioFile> {
        requestedFolder = folderId
        gate?.await()
        return files
    }
    override suspend fun audioFile(id: String) = file
    override suspend fun saveFolder(id: String?, name: String, parentId: String?): Folder {
        createdParent = parentId
        failure?.let { throw it }
        return Folder(id ?: "new", validatedName(name), parentId)
    }
    override suspend fun saveTag(id: String?, name: String) = Tag(id ?: "new", validatedName(name))
    override suspend fun deleteFolder(id: String) { failure?.let { throw it }; folderList = folderList.filter { it.id != id } }
    override suspend fun deleteTag(id: String) { tagList = tagList.filter { it.id != id } }
    override suspend fun move(id: String, folderId: String?): AudioFile { gate?.await(); failure?.let { throw it }; return file }
    override suspend fun moveMany(ids: Set<String>, folderId: String?): List<AudioFile> { move(ids.first(), folderId); return files }
    override suspend fun assignTags(id: String, tagIds: Set<String>): AudioFile { failure?.let { throw it }; return file.copy(tags = tagList.filter { it.id in tagIds }) }
}
