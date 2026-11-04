package dev.alnoer.audiosaas.feature.preferences

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {
    val isFirstTimeLaunch = appPreferences.isFirstTimeLaunch
}
