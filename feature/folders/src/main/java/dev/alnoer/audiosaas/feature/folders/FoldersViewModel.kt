package dev.alnoer.audiosaas.feature.folders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.alnoer.audiosaas.library.AudioFile
import dev.alnoer.audiosaas.library.Folder
import dev.alnoer.audiosaas.library.LibraryRepository
import dev.alnoer.audiosaas.library.Tag
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FoldersState(
    val folders: List<Folder> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val children: List<Folder> = emptyList(),
    val audio: List<AudioFile> = emptyList(),
    val loading: Boolean = true,
    val busy: Boolean = false,
    val error: String? = null,
    val editError: String? = null,
    val revision: Int = 0,
)

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val repository: LibraryRepository,
    private val saved: SavedStateHandle,
) : ViewModel() {
    private val mutable = MutableStateFlow(FoldersState())
    val state = mutable.asStateFlow()
    val parentId get() = saved.get<String>("parentId")
    private var load: Job? = null
    internal var workScope = viewModelScope

    fun open(id: String?) {
        saved["parentId"] = id
        refresh()
    }

    fun refresh() {
        load?.cancel()
        load = workScope.launch {
            mutable.value = mutable.value.copy(loading = true, error = null)
            try {
                val folders = repository.folders()
                if (parentId != null && folders.none { it.id == parentId }) saved["parentId"] = null
                val children = repository.children(parentId)
                val audio = repository.audio(folderId = parentId ?: "null")
                val tags = repository.tags()
                mutable.value = mutable.value.copy(folders = folders, children = children, audio = audio, tags = tags, loading = false)
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                mutable.value = mutable.value.copy(loading = false, error = e.message ?: "Не удалось загрузить библиотеку.")
            }
        }
    }

    fun save(tag: Boolean, id: String?, name: String) = mutate {
        if (tag) repository.saveTag(id, name) else repository.saveFolder(id, name, parentId)
    }

    fun delete(tag: Boolean, id: String) = mutate {
        if (tag) repository.deleteTag(id) else repository.deleteFolder(id)
    }

    fun clearEditError() { mutable.value = mutable.value.copy(editError = null) }

    private fun mutate(action: suspend () -> Unit) {
        if (mutable.value.busy) return
        workScope.launch {
            mutable.value = mutable.value.copy(busy = true, editError = null)
            try {
                action()
                mutable.value = mutable.value.copy(busy = false, revision = mutable.value.revision + 1)
                refresh()
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                mutable.value = mutable.value.copy(busy = false, editError = e.message ?: "Не удалось сохранить.")
            }
        }
    }
}
