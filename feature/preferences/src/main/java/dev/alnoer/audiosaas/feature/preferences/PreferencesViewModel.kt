package dev.alnoer.audiosaas.feature.preferences

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
) : ViewModel() {
    val isFirstTimeLaunch: Flow<Boolean?> = appPreferences.isFirstTimeLaunch

    suspend fun setNotFirstTimeLaunch() {
        appPreferences.setNotFirstTimeLaunch()
    }
}
