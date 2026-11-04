package dev.alnoer.audiosaas.feature.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthTokensTest {
    @Test
    fun futureExpirationIsValid() {
        val tokens = tokens(expiresAtMillis = 1_001L)

        assertTrue(tokens.isAccessTokenValid(nowMillis = 1_000L))
    }

    @Test
    fun reachedExpirationIsInvalid() {
        val tokens = tokens(expiresAtMillis = 1_000L)

        assertFalse(tokens.isAccessTokenValid(nowMillis = 1_000L))
    }

    private fun tokens(expiresAtMillis: Long) = AuthTokens(
        accessToken = "access",
        refreshToken = "refresh",
        accessTokenExpiresAtMillis = expiresAtMillis,
    )
}
