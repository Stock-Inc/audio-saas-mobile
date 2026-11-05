package dev.alnoer.audiosaas.feature.upload

import dev.alnoer.audiosaas.feature.upload.data.resolveUploadUrl
import dev.alnoer.audiosaas.feature.upload.data.safeUploadFileName
import dev.alnoer.audiosaas.feature.upload.data.uploadFailureMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FileUploadRepositoryTest {

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
