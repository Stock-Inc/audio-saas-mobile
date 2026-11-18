package dev.alnoer.audiosaas.feature.upload

import android.content.Context
import android.media.MediaRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

internal interface VoiceRecorder {
    fun start(): File
    fun stop()
    fun release()
}

internal class AndroidVoiceRecorder @Inject constructor(
    @ApplicationContext private val context: Context,
) : VoiceRecorder {
    private var recorder: MediaRecorder? = null

    override fun start(): File {
        check(recorder == null)
        val directory = File(context.cacheDir, "voice-recordings")
        check(directory.isDirectory || directory.mkdirs()) { "Нет места для записи." }
        // shortcut: drafts are session-only; add persistent recovery when drafts are supported.
        directory.listFiles()?.forEach { it.delete() }
        return try {
            startRecording(directory, webm = true)
        } catch (exception: SecurityException) {
            throw exception
        } catch (_: Exception) {
            // Vorbis encoding is optional on Android devices.
            startRecording(directory, webm = false)
        }
    }

    private fun startRecording(directory: File, webm: Boolean): File {
        val file = File.createTempFile("recording-", if (webm) ".webm" else ".m4a", directory)
        val active = MediaRecorder(context)
        recorder = active
        try {
            active.setAudioSource(MediaRecorder.AudioSource.MIC)
            active.setOutputFormat(if (webm) MediaRecorder.OutputFormat.WEBM else MediaRecorder.OutputFormat.MPEG_4)
            active.setAudioEncoder(if (webm) MediaRecorder.AudioEncoder.VORBIS else MediaRecorder.AudioEncoder.AAC)
            active.setAudioChannels(1)
            active.setAudioSamplingRate(44100)
            active.setAudioEncodingBitRate(96000)
            active.setOutputFile(file.absolutePath)
            active.prepare()
            active.start()
            return file
        } catch (exception: Exception) {
            release()
            file.delete()
            throw exception
        }
    }

    override fun stop() {
        try {
            recorder?.stop()
        } finally {
            release()
        }
    }

    override fun release() {
        recorder?.release()
        recorder = null
    }
}

internal fun voiceRecordingContentType(file: File): String =
    if (file.extension == "webm") "audio/webm" else "audio/mp4"
