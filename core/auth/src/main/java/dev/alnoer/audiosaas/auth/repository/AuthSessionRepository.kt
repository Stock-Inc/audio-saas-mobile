package dev.alnoer.audiosaas.auth.repository

import dev.alnoer.audiosaas.auth.model.AuthTokens
import kotlinx.coroutines.flow.Flow

interface AuthSessionRepository {
    val tokens: Flow<AuthTokens?>
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun clearTokens()
}

interface AccessTokenProvider {
    suspend fun validAccessToken(): String?
}
