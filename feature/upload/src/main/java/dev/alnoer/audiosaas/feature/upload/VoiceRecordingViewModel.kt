package dev.alnoer.audiosaas.feature.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal enum class RecordingPhase { Ready, Recording, Review, Uploading, Success }

internal data class VoiceRecordingState(
    val phase: RecordingPhase = RecordingPhase.Ready,
    val seconds: Long = 0,
    val file: File? = null,
    val error: String? = null,
)

@HiltViewModel
internal class VoiceRecordingViewModel internal constructor(
    private val recorder: VoiceRecorder,
    private val uploadFile: UploadFileUseCase,
    private val now: () -> Long,
    private val scope: CoroutineScope? = null,
) : ViewModel() {
    @Inject
    constructor(recorder: AndroidVoiceRecorder, uploadFile: UploadFileUseCase) :
        this(recorder, uploadFile, { android.os.SystemClock.elapsedRealtime() })

    private val mutableState = MutableStateFlow(VoiceRecordingState())
    val state = mutableState.asStateFlow()
    private var startedAt = 0L
    private var timer: Job? = null

    fun start() {
        if (state.value.phase != RecordingPhase.Ready) return
        try {
            val file = recorder.start()
            startedAt = now()
            mutableState.value = VoiceRecordingState(RecordingPhase.Recording, file = file)
            timer = (scope ?: viewModelScope).launch {
                while (state.value.phase == RecordingPhase.Recording) {
                    mutableState.value = state.value.copy(seconds = (now() - startedAt) / 1000)
                    delay(250)
                }
            }
        } catch (_: Exception) {
            mutableState.value = VoiceRecordingState(error = "Не удалось включить микрофон. Проверьте разрешение и свободное место.")
        }
    }

    fun stop() {
        if (state.value.phase != RecordingPhase.Recording) return
        timer?.cancel()
        try {
            recorder.stop()
            check((state.value.file?.length() ?: 0) > 0)
            mutableState.value = state.value.copy(phase = RecordingPhase.Review)
        } catch (_: Exception) {
            discard()
            mutableState.value = state.value.copy(error = "Запись слишком короткая или прервана. Попробуйте ещё раз.")
        }
    }

    fun discard() {
        if (state.value.phase == RecordingPhase.Uploading) return
        timer?.cancel()
        recorder.release()
        state.value.file?.delete()
        mutableState.value = VoiceRecordingState()
    }

    fun upload() {
        if (state.value.phase != RecordingPhase.Review) return
        val file = state.value.file ?: return
        mutableState.value = state.value.copy(phase = RecordingPhase.Uploading, error = null)
        (scope ?: viewModelScope).launch {
            try {
                uploadFile(UploadFile(file.name, voiceRecordingContentType(file), file.length(), file::inputStream))
                file.delete()
                mutableState.value = state.value.copy(phase = RecordingPhase.Success, file = null)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                mutableState.value = state.value.copy(
                    phase = RecordingPhase.Review,
                    error = exception.message ?: "Не удалось загрузить запись. Попробуйте ещё раз.",
                )
            }
        }
    }

    override fun onCleared() {
        timer?.cancel()
        recorder.release()
        state.value.file?.delete()
    }
}
