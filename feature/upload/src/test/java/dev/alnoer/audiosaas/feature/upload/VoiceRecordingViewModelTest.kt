package dev.alnoer.audiosaas.feature.upload

import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test

class VoiceRecordingViewModelTest {
    @Test
    fun repeatedStartAndStopAreIgnoredAndDiscardDeletesAudio() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val recorder = FakeRecorder()
        val model = createModel(recorder, scope)
        try {
            model.start()
            model.start()
            assertEquals(1, recorder.starts)
            model.stop()
            model.stop()
            assertEquals(1, recorder.stops)
            assertEquals(RecordingPhase.Review, model.state.value.phase)
            assertTrue(recorder.file.exists())
            model.discard()
            assertFalse(recorder.file.exists())
            assertEquals(RecordingPhase.Ready, model.state.value.phase)
        } finally {
            scope.cancel()
            recorder.file.delete()
        }
    }

    @Test
    fun failedStopDiscardsInvalidAudio() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val recorder = FakeRecorder(failStop = true)
        val model = createModel(recorder, scope)
        try {
            model.start()
            model.stop()
            assertEquals(RecordingPhase.Ready, model.state.value.phase)
            assertNotNull(model.state.value.error)
            assertFalse(recorder.file.exists())
        } finally {
            scope.cancel()
            recorder.file.delete()
        }
    }

    @Test
    fun failedUploadRetainsAudioAndRetryDeletesItAfterSuccess() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val recorder = FakeRecorder()
        var attempts = 0
        val repository = object : FileUploadRepository {
            override suspend fun upload(file: UploadFile) {
                assertEquals("audio/mp4", file.contentType)
                assertEquals(3L, file.size)
                assertEquals(3, file.openStream().use { it.readBytes().size })
                attempts++
                if (attempts == 1) error("Offline")
            }
        }
        val model = createModel(recorder, scope, repository)
        try {
            model.start()
            model.stop()
            model.upload()
            withTimeout(5000) { model.state.first { it.error != null } }
            assertTrue(recorder.file.exists())
            model.upload()
            withTimeout(5000) { model.state.first { it.phase == RecordingPhase.Success } }
            assertEquals(2, attempts)
            assertFalse(recorder.file.exists())
        } finally {
            scope.cancel()
            recorder.file.delete()
        }
    }

    private fun createModel(
        recorder: VoiceRecorder,
        scope: CoroutineScope,
        repository: FileUploadRepository = object : FileUploadRepository {
            override suspend fun upload(file: UploadFile) = Unit
        },
    ) = VoiceRecordingViewModel(
        recorder,
        UploadFileUseCase(object : UploadFileReader {
            override fun read(uri: String): UploadFile = error("Voice upload must not resolve a URI")
        }, repository),
        { 0L },
        scope,
    )

    private class FakeRecorder(private val failStop: Boolean = false) : VoiceRecorder {
        val file = File.createTempFile("voice-test", ".m4a")
        var starts = 0
        var stops = 0
        override fun start(): File {
            starts++
            file.writeBytes(byteArrayOf(1, 2, 3))
            return file
        }
        override fun stop() {
            stops++
            if (failStop) error("Too short")
        }
        override fun release() = Unit
    }
}
