package dev.alnoer.audiosaas.auth.data

import dev.alnoer.audiosaas.auth.model.AuthTokens
import dev.alnoer.audiosaas.local.keyvalue.KeyValueStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthSessionRepositoryImplTest {

    @Test
    fun validAccessTokenReturnsStoredToken() = runBlocking {
        val repository = repositoryWithExpiration(Long.MAX_VALUE)

        assertEquals("access", repository.validAccessToken())
    }

    @Test
    fun validAccessTokenRejectsExpiredToken() = runBlocking {
        val repository = repositoryWithExpiration(1L)

        assertNull(repository.validAccessToken())
    }

    private suspend fun repositoryWithExpiration(expiration: Long): AuthSessionRepositoryImpl {
        val repository = AuthSessionRepositoryImpl(FakeKeyValueStore())
        repository.saveTokens(
            AuthTokens(
                accessToken = "access",
                refreshToken = "refresh",
                accessTokenExpiresAtMillis = expiration,
            ),
        )
        return repository
    }
}

private class FakeKeyValueStore : KeyValueStore {
    private val values = MutableStateFlow<Map<String, String>>(emptyMap())

    override fun observeBoolean(key: String): Flow<Boolean?> =
        error("Not needed by this test")

    override suspend fun setBoolean(key: String, value: Boolean) =
        error("Not needed by this test")

    override fun observeStrings(keys: Set<String>): Flow<Map<String, String>> =
        values.map { current -> current.filterKeys(keys::contains) }

    override suspend fun putStrings(values: Map<String, String>) {
        this.values.value += values
    }

    override suspend fun remove(keys: Set<String>) {
        values.value -= keys
    }
}
