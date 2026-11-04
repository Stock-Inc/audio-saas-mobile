package dev.alnoer.audiosaas.feature.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val authState = authRepository.authState

    fun getLoginIntent() = authRepository.getLoginIntent()
    fun processAuthResult(intent: Intent) = authRepository.onAuthResult(intent)
}
