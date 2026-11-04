package dev.alnoer.audiosaas.local.keyvalue

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.forEach

interface KeyValueStore {
    fun observeBoolean(key: String): Flow<Boolean?>
    suspend fun setBoolean(key: String, value: Boolean)
    fun observeStrings(keys: Set<String>): Flow<Map<String, String>>
    suspend fun putStrings(values: Map<String, String>)
    suspend fun remove(keys: Set<String>)
}

@Singleton
class KeyValueStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : KeyValueStore {
    override fun observeBoolean(key: String): Flow<Boolean?> {
        requireValidKey(key)
        val preferenceKey = booleanPreferencesKey(key)

        return dataStore.data.map { preferences ->
            preferences[preferenceKey]
        }
    }

    override suspend fun setBoolean(key: String, value: Boolean) {
        requireValidKey(key)

        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    override fun observeStrings(keys: Set<String>): Flow<Map<String, String>> {
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

    override suspend fun putStrings(values: Map<String, String>) {
        requireValidKeys(values.keys)

        dataStore.edit { preferences ->
            values.forEach { (key, value) ->
                preferences[stringPreferencesKey(key)] = value
            }
        }
    }

    override suspend fun remove(keys: Set<String>) {
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

    private fun requireValidKey(key: String) {
        require(key.isNotBlank()) { "Key must not be blank" }
    }
}
