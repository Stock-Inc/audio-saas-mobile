package dev.alnoer.audiosaas.feature.auth.data.repository

import android.content.Intent
import dev.alnoer.audiosaas.feature.auth.data.remote.KeycloakAuthResult
import dev.alnoer.audiosaas.feature.auth.data.remote.KeycloakIntegration
import dev.alnoer.audiosaas.feature.auth.domain.model.AuthState
import dev.alnoer.audiosaas.feature.auth.domain.repository.AuthRepository
import dev.alnoer.audiosaas.feature.auth.domain.repository.LocalAuthRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
