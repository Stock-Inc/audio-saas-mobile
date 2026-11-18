package dev.alnoer.audiosaas.feature.auth

import dev.alnoer.audiosaas.feature.auth.data.remote.tokenExchangeFailureMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenExchangeFailureTest {
    @Test
    fun clockSkewAndExpiryExplainHowToRecover() {
        val expired = tokenExchangeFailureMessage("ID Token expired")
        assertTrue(expired.contains("дату и время"))
        assertEquals(expired, tokenExchangeFailureMessage(
            "Issued at time is more than 10 minutes before or after the current time",
        ))
    }

    @Test
    fun unknownErrorsNeverExposeRawServerContent() {
        assertEquals(
            "Не удалось получить токены. Повторите вход.",
            tokenExchangeFailureMessage("sensitive server response"),
        )
    }
}
