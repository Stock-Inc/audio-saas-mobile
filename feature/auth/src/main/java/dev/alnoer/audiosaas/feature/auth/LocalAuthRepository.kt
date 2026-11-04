package dev.alnoer.audiosaas.feature.auth

import dev.alnoer.audiosaas.local.keyvalue.KeyValueStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LocalAuthRepository {
    val tokens: Flow<AuthTokens?>
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun clearTokens()
}

@Singleton
class LocalAuthRepositoryImpl @Inject constructor(
    private val keyValueStore: KeyValueStore,
): LocalAuthRepository {
    override val tokens: Flow<AuthTokens?> =
        keyValueStore.observeStrings(TOKEN_KEYS).map { values ->
            val accessToken = values[ACCESS_TOKEN] ?: return@map null
            val refreshToken = values[REFRESH_TOKEN] ?: return@map null
            val accessTokenExpiresAtMillis =
                values[ACCESS_TOKEN_EXPIRES_AT]?.toLongOrNull() ?: return@map null

            AuthTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
                accessTokenExpiresAtMillis = accessTokenExpiresAtMillis,
            )
        }

    override suspend fun saveTokens(tokens: AuthTokens) {
        requireValidToken(tokens.accessToken, "Access")
        requireValidToken(tokens.refreshToken, "Refresh")
        require(tokens.accessTokenExpiresAtMillis > 0) {
            "Access token expiration must be positive"
        }

        keyValueStore.putStrings(
            mapOf(
                ACCESS_TOKEN to tokens.accessToken,
                REFRESH_TOKEN to tokens.refreshToken,
                ACCESS_TOKEN_EXPIRES_AT to tokens.accessTokenExpiresAtMillis.toString(),
            ),
        )
    }

    override suspend fun clearTokens() {
        keyValueStore.remove(TOKEN_KEYS)
    }

    private fun requireValidToken(token: String, name: String) {
        require(token.isNotBlank()) { "$name token must not be blank" }
    }

    private companion object {
        const val ACCESS_TOKEN = "auth.access_token"
        const val REFRESH_TOKEN = "auth.refresh_token"
        const val ACCESS_TOKEN_EXPIRES_AT = "auth.access_token_expires_at"
        val TOKEN_KEYS = setOf(ACCESS_TOKEN, REFRESH_TOKEN, ACCESS_TOKEN_EXPIRES_AT)
    }
}
