package dev.alnoer.audiosaas.feature.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UploadUiState {
    data object Idle : UploadUiState
    data object Uploading : UploadUiState
    data object Success : UploadUiState
    data class Failure(val message: String) : UploadUiState
}

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repository: FileUploadRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState.Idle)
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    fun upload(uri: Uri) {
        if (_uiState.value == UploadUiState.Uploading) return

        _uiState.value = UploadUiState.Uploading
        viewModelScope.launch {
            try {
                repository.upload(uri)
                _uiState.value = UploadUiState.Success
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = UploadUiState.Failure(
                    exception.message ?: "Не удалось загрузить файл.",
                )
            }
        }
    }
}
