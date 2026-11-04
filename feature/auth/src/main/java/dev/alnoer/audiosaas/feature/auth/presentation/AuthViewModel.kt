package dev.alnoer.audiosaas.feature.auth.presentation

import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.alnoer.audiosaas.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val authState = authRepository.authState

    fun getLoginIntent() = authRepository.getLoginIntent()
    fun processAuthResult(intent: Intent) = authRepository.onAuthResult(intent)
}
