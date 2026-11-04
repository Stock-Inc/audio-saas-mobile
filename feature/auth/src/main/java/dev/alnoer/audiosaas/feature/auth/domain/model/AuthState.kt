package dev.alnoer.audiosaas.feature.auth.domain.model

sealed interface AuthState {
    data object NotStarted : AuthState
    data object InProgress : AuthState
    data object Success : AuthState
    data class Failure(val message: String) : AuthState
}
