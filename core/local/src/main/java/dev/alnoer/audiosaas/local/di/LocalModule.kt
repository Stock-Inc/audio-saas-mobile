package dev.alnoer.audiosaas.local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.tink.AeadSerializer
import androidx.room3.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.alnoer.audiosaas.local.encryption.EncryptionProvider.getOrCreateAead
import dev.alnoer.audiosaas.local.keyvalue.PreferencesStreamSerializer
import dev.alnoer.audiosaas.local.room.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideKeyValueDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        DataStoreFactory.create(
            serializer = AeadSerializer(
                aead = getOrCreateAead(KEY_ALIAS),
                wrappedSerializer = PreferencesStreamSerializer,
                associatedData = DATA_STORE_FILE_NAME.encodeToByteArray(),
            ),
        ) {
            context.preferencesDataStoreFile(DATA_STORE_NAME)
        }

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .build()

    private const val DATA_STORE_NAME = "key_value_store"
    private const val DATA_STORE_FILE_NAME = "$DATA_STORE_NAME.preferences_pb"
    private const val KEY_ALIAS = "key_value_store_alias"
}
