package dev.alnoer.audiosaas.local.keyvalue

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class KeyValueStoreTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var dataStoreScope: CoroutineScope
    private lateinit var store: KeyValueStore

    @Before
    fun setUp() {
        dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        store = KeyValueStore(
            dataStore = PreferenceDataStoreFactory.create(
                scope = dataStoreScope,
                produceFile = {
                    File(temporaryFolder.root, "key_value_store.preferences_pb")
                },
            ),
        )
    }

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun stringsCanBeStoredUpdatedAndRemoved() = runBlocking {
        val keys = setOf("first", "second")
        assertEquals(emptyMap<String, String>(), store.observeStrings(keys).first())

        store.putStrings(
            mapOf(
                "first" to "one",
                "second" to "two",
            ),
        )
        assertEquals(
            mapOf(
                "first" to "one",
                "second" to "two",
            ),
            store.observeStrings(keys).first(),
        )

        store.putStrings(mapOf("first" to "updated"))
        store.remove(setOf("second"))
        assertEquals(
            mapOf("first" to "updated"),
            store.observeStrings(keys).first(),
        )
    }
}
