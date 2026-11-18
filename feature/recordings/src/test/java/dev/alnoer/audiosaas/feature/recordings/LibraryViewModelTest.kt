package dev.alnoer.audiosaas.feature.recordings

import androidx.lifecycle.SavedStateHandle
import dev.alnoer.audiosaas.library.*
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

class LibraryViewModelTest {
    @Test
    fun readableMetadataHandlesUnexpectedDatesAndFolderPaths() {
        val date = recordingDate("2026-11-18T09:00:00Z")
        assertTrue(date.contains("2026"))
        assertFalse(date.contains("T09:"))
        assertEquals("unknown", recordingDate("unknown"))
        val folders = listOf(Folder("root", "Meetings", null), Folder("child", "Work", "root"))
        assertEquals("Meetings / Work", folderPath("child", folders))
        assertEquals("", folderPath("missing", folders))
    }

    @Test
    fun failedMoveKeepsSelectionAndSuccessfulRetryClearsIt() {
        val repository = FakeLibrary()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        try {
            val model = LibraryViewModel(repository, SavedStateHandle()).also { it.workScope = scope }
            model.refresh()
            model.toggle("audio")
            repository.failure = LibraryException(404, "Missing")
            model.move(setOf("audio"), null)
            assertEquals(setOf("audio"), model.state.value.selected)
            assertEquals("Missing", model.state.value.editError)
            repository.failure = null
            model.move(setOf("audio"), null)
            assertTrue(model.state.value.selected.isEmpty())
            assertEquals(1, model.state.value.revision)
        } finally { scope.cancel() }
    }

    @Test
    fun deletedFiltersResetAndNewFilterCancelsOldRequest() {
        val repository = FakeLibrary()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        try {
            val model = LibraryViewModel(repository, SavedStateHandle()).also { it.workScope = scope }
            model.filter("deleted", "deleted")
            assertEquals("null", model.folderId)
            assertNull(model.tagId)
            repository.gate = CompletableDeferred()
            model.filter("parent", "tag")
            val oldGate = repository.gate
            repository.gate = null
            repository.files = emptyList()
            model.filter(null, null)
            oldGate?.complete(Unit)
            assertTrue(model.state.value.audio.isEmpty())
            assertNull(repository.requestedFolder)
        } finally { scope.cancel() }
    }

    @Test
    fun tagClearingAndDuplicateSubmission() {
        val repository = FakeLibrary()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        try {
            val model = LibraryViewModel(repository, SavedStateHandle()).also { it.workScope = scope }
            model.refresh()
            model.assignTags("audio", emptySet())
            assertEquals(1, model.state.value.revision)
            repository.gate = CompletableDeferred()
            model.move(setOf("audio"), null)
            model.move(setOf("audio"), null)
            assertTrue(model.state.value.busy)
            repository.gate?.complete(Unit)
            assertEquals(2, model.state.value.revision)
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
