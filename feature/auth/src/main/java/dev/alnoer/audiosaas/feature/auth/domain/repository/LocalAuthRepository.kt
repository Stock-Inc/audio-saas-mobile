package dev.alnoer.audiosaas.feature.auth.domain.repository

import dev.alnoer.audiosaas.feature.auth.domain.model.AuthTokens
import kotlinx.coroutines.flow.Flow

interface LocalAuthRepository {
    val tokens: Flow<AuthTokens?>
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun clearTokens()
}
