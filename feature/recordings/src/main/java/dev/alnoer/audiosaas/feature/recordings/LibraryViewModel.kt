package dev.alnoer.audiosaas.feature.recordings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.alnoer.audiosaas.library.AudioFile
import dev.alnoer.audiosaas.library.AudioSort
import dev.alnoer.audiosaas.library.Folder
import dev.alnoer.audiosaas.library.LibraryRepository
import dev.alnoer.audiosaas.library.Tag
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LibraryState(
    val audio: List<AudioFile> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val selected: Set<String> = emptySet(),
    val loading: Boolean = true,
    val busy: Boolean = false,
    val error: String? = null,
    val editError: String? = null,
    val revision: Int = 0,
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: LibraryRepository,
    private val saved: SavedStateHandle,
) : ViewModel() {
    private val mutable = MutableStateFlow(LibraryState())
    val state = mutable.asStateFlow()
    val folderId get() = saved.get<String>("folderId")
    val tagId get() = saved.get<String>("tagId")
    val sort get() = AudioSort.valueOf(saved.get<String>("sort") ?: AudioSort.NEWEST.name)
    private var load: Job? = null
    internal var workScope = viewModelScope

    fun filter(folder: String?, tag: String?, order: AudioSort = sort) {
        saved["folderId"] = folder
        saved["tagId"] = tag
        saved["sort"] = order.name
        mutable.value = mutable.value.copy(selected = emptySet())
        refresh()
    }

    fun toggle(id: String) {
        val selected = mutable.value.selected
        mutable.value = mutable.value.copy(selected = if (id in selected) selected - id else selected + id)
    }

    fun clearSelection() { mutable.value = mutable.value.copy(selected = emptySet()) }
    fun clearEditError() { mutable.value = mutable.value.copy(editError = null) }

    fun refresh() {
        load?.cancel()
        load = workScope.launch {
            mutable.value = mutable.value.copy(loading = true, error = null)
            try {
                val folders = repository.folders()
                val tags = repository.tags()
                if (folderId != null && folderId != "null" && folders.none { it.id == folderId }) saved["folderId"] = "null"
                if (tagId != null && tags.none { it.id == tagId }) saved["tagId"] = null
                val id = saved.get<String>("audioId")
                val audio = if (id == null) repository.audio(folderId, tagId, sort) else listOf(repository.audioFile(id))
                mutable.value = mutable.value.copy(audio = audio, folders = folders, tags = tags,
                    selected = mutable.value.selected.intersect(audio.map { it.id }.toSet()), loading = false)
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                mutable.value = mutable.value.copy(loading = false, error = e.message ?: "Не удалось загрузить записи.")
            }
        }
    }

    fun move(ids: Set<String>, folderId: String?) = mutate {
        if (ids.size == 1) repository.move(ids.single(), folderId) else repository.moveMany(ids, folderId)
    }

    fun assignTags(id: String, tags: Set<String>) = mutate { repository.assignTags(id, tags) }

    private fun mutate(action: suspend () -> Unit) {
        if (mutable.value.busy) return
        workScope.launch {
            mutable.value = mutable.value.copy(busy = true, editError = null)
            try {
                action()
                mutable.value = mutable.value.copy(busy = false, selected = emptySet(), revision = mutable.value.revision + 1)
                refresh()
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                mutable.value = mutable.value.copy(busy = false, editError = e.message ?: "Не удалось сохранить.")
            }
        }
    }
}
