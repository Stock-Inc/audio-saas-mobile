package dev.alnoer.audiosaas.feature.auth.presentation

import dev.alnoer.audiosaas.auth.model.AuthTokens
import dev.alnoer.audiosaas.auth.repository.AuthSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import kotlin.time.Duration.Companion.seconds
import org.junit.Test

class AuthSessionViewModelTest {
    @Test
    fun sessionExpiresWithoutAnotherTokenWrite() = runBlocking {
        val repository = FakeLocalAuthRepository(
            AuthTokens(
                accessToken = "access",
                refreshToken = "refresh",
                accessTokenExpiresAtMillis = System.currentTimeMillis() + 500L,
            ),
        )
        val viewModel = AuthSessionViewModel(repository)

        val states = withTimeout(2.seconds) {
            viewModel.isAuthenticated.take(2).toList()
        }

        assertEquals(listOf(true, false), states)
    }

    private class FakeLocalAuthRepository(
        initialTokens: AuthTokens?,
    ) : AuthSessionRepository {
        override val tokens: Flow<AuthTokens?> = MutableStateFlow(initialTokens)

        override suspend fun saveTokens(tokens: AuthTokens) = Unit

        override suspend fun clearTokens() = Unit
    }
}
