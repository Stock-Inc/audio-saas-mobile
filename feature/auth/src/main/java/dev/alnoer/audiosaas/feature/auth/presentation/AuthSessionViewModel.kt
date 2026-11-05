package dev.alnoer.audiosaas.feature.auth.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.alnoer.audiosaas.auth.repository.AuthSessionRepository
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class AuthSessionViewModel @Inject constructor(
    authSessionRepository: AuthSessionRepository,
) : ViewModel() {
    val isAuthenticated: Flow<Boolean?> = channelFlow {
        authSessionRepository.tokens.collectLatest { tokens ->
            val remainingMillis = tokens
                ?.let { it.accessTokenExpiresAtMillis - System.currentTimeMillis() }
                ?: 0L

            if (remainingMillis <= 0L) {
                send(false)
                return@collectLatest
            }

            send(true)
            delay(remainingMillis.milliseconds)
            send(false)
        }
    }
}
