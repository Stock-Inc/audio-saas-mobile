package dev.alnoer.audiosaas.feature.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@HiltViewModel
class AuthSessionViewModel @Inject constructor(
    localAuthRepository: LocalAuthRepository,
) : ViewModel() {
    val isAuthenticated: Flow<Boolean?> = localAuthRepository.tokens
        .map { tokens -> tokens?.isAccessTokenValid() == true }
}
