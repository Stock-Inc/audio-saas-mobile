package dev.alnoer.audiosaas.feature.auth.domain.repository

import android.content.Intent
import dev.alnoer.audiosaas.feature.auth.domain.model.AuthState
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>
    fun onAuthResult(intent: Intent)
    fun getLoginIntent(): Intent?
}
