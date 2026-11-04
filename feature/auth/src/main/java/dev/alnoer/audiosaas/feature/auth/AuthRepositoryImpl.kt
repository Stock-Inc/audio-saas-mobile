package dev.alnoer.audiosaas.feature.auth

import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val authState: StateFlow<AuthState>
    fun onAuthResult(intent: Intent)
    fun getLoginIntent(): Intent?
}

sealed interface AuthState {
    data object NotStarted : AuthState
    data object InProgress : AuthState
    data object Success : AuthState
    data class Failure(val message: String) : AuthState
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val keycloakIntegration: KeycloakIntegration,
    private val localAuthRepository: LocalAuthRepository,
) : AuthRepository {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _authState = MutableStateFlow<AuthState>(AuthState.NotStarted)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override fun onAuthResult(intent: Intent) {
        _authState.value = AuthState.InProgress

        keycloakIntegration.processResult(intent) { result ->
            when (result) {
                is KeycloakAuthResult.Failure -> {
                    _authState.value = AuthState.Failure(result.message)
                }

                is KeycloakAuthResult.Success -> {
                    repositoryScope.launch {
                        try {
                            localAuthRepository.saveTokens(result.tokens)
                            _authState.value = AuthState.Success
                        } catch (exception: Exception) {
                            _authState.value = AuthState.Failure(
                                exception.message ?: "Failed to save authentication tokens",
                            )
                        }
                    }
                }
            }
        }
    }

    override fun getLoginIntent(): Intent? = keycloakIntegration.getLoginIntent()
}
