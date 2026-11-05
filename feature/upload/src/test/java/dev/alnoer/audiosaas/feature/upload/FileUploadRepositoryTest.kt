package dev.alnoer.audiosaas.feature.upload

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FileUploadRepositoryTest {

    @Test
    fun configuredUploadUrlAcceptsHttpEndpoints() {
        assertEquals(
            "https://example.test/upload",
            requireConfiguredUploadUrl("https://example.test/upload"),
        )
    }

    @Test
    fun configuredUploadUrlRejectsMissingEndpoint() {
        assertThrows(IllegalArgumentException::class.java) {
            requireConfiguredUploadUrl("")
        }
    }

    @Test
    fun fileNameRemovesMultipartHeaderCharacters() {
        assertEquals("recording___.mp3", safeUploadFileName("recording\r\n\".mp3"))
    }
}
