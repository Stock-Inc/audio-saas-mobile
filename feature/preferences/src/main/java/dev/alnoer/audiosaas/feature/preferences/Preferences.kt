package dev.alnoer.audiosaas.feature.preferences

import dev.alnoer.audiosaas.local.keyvalue.KeyValueStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface AppPreferences {
    val isFirstTimeLaunch: Flow<Boolean>
    suspend fun setNotFirstTimeLaunch()
}

@Singleton
class AppPreferencesImpl @Inject constructor(
    private val keyValueStore: KeyValueStore
): AppPreferences {
    override val isFirstTimeLaunch: Flow<Boolean> =
        keyValueStore.observeBoolean(FIRST_TIME_LAUNCH).map {
            it ?: true
        }

    override suspend fun setNotFirstTimeLaunch() {
        keyValueStore.setBoolean(FIRST_TIME_LAUNCH, false)
    }

    private companion object {
        const val FIRST_TIME_LAUNCH = "app.first_time_launch"
    }
}
