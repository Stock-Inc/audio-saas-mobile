package dev.alnoer.audiosaas.local.keyvalue

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class KeyValueStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    fun observeStrings(keys: Set<String>): Flow<Map<String, String>> {
        requireValidKeys(keys)
        val preferenceKeys = keys.associateWith(::stringPreferencesKey)

        return dataStore.data.map { preferences ->
            buildMap {
                preferenceKeys.forEach { (key, preferenceKey) ->
                    preferences[preferenceKey]?.let { value ->
                        put(key, value)
                    }
                }
            }
        }
    }

    suspend fun putStrings(values: Map<String, String>) {
        requireValidKeys(values.keys)

        dataStore.edit { preferences ->
            values.forEach { (key, value) ->
                preferences[stringPreferencesKey(key)] = value
            }
        }
    }

    suspend fun remove(keys: Set<String>) {
        requireValidKeys(keys)

        dataStore.edit { preferences ->
            keys.forEach { key ->
                preferences.remove(stringPreferencesKey(key))
            }
        }
    }

    private fun requireValidKeys(keys: Set<String>) {
        require(keys.none(String::isBlank)) { "Keys must not be blank" }
    }
}
