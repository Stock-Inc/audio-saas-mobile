package dev.alnoer.audiosaas.feature.upload

import dev.alnoer.audiosaas.feature.upload.data.resolveUploadUrl
import dev.alnoer.audiosaas.feature.upload.data.safeUploadFileName
import dev.alnoer.audiosaas.feature.upload.data.uploadFailureMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import dev.alnoer.audiosaas.feature.upload.data.asMultipartContent
import io.ktor.http.content.PartData
import java.io.ByteArrayInputStream

class FileUploadRepositoryTest {
    @OptIn(io.ktor.utils.io.InternalAPI::class)
    @Test
    fun multipartContainsOnlyFileAndExplicitProcessingOptions() {
        val file = UploadFile("voice.webm", "audio/webm", 0) { ByteArrayInputStream(byteArrayOf()) }
        val parts = file.asMultipartContent().parts
        assertEquals(listOf("options", "file"), parts.map { it.name })
        val options = parts.first() as PartData.FormItem
        assertEquals("""{"diarization":true,"summary":true,"tasks":true}""", options.value)
        assertEquals("application/json", options.headers["Content-Type"])
        assertEquals("audio/webm", parts.last().headers["Content-Type"])
        parts.forEach { it.dispose() }
    }

    @Test
    fun recordingMimeTypeMatchesContainer() {
        assertEquals("audio/webm", voiceRecordingContentType(java.io.File("voice.webm")))
        assertEquals("audio/mp4", voiceRecordingContentType(java.io.File("voice.m4a")))
    }


    @Test
    fun configuredUploadUrlAppendsAudioPath() {
        assertEquals(
            "https://example.test/api/v1/audio",
            resolveUploadUrl("https://example.test"),
        )
    }

    @Test
    fun configuredUploadUrlPreservesFullEndpoint() {
        assertEquals(
            "https://example.test/api/v1/audio",
            resolveUploadUrl("https://example.test/api/v1/audio/"),
        )
    }

    @Test
    fun configuredUploadUrlRejectsMissingEndpoint() {
        assertThrows(IllegalArgumentException::class.java) {
            resolveUploadUrl("")
        }
    }

    @Test
    fun uploadErrorExplainsAuthenticationRedirect() {
        assertEquals(
            "Сервер перенаправил запрос на страницу входа.",
            uploadFailureMessage(302),
        )
    }

    @Test
    fun uploadErrorExplainsOversizedFile() {
        assertEquals(
            "Файл превышает допустимый размер.",
            uploadFailureMessage(413),
        )
    }

    @Test
    fun fileNameRemovesMultipartHeaderCharacters() {
        assertEquals("recording___.mp3", safeUploadFileName("recording\r\n\".mp3"))
    }
}
