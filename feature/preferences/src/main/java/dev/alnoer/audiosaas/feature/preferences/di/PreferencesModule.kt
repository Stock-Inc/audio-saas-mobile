package dev.alnoer.audiosaas.feature.preferences.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.alnoer.audiosaas.feature.preferences.AppPreferences
import dev.alnoer.audiosaas.feature.preferences.AppPreferencesImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {
    @Binds
    @Singleton
    abstract fun bindPreferences(appPreferencesImpl: AppPreferencesImpl): AppPreferences
}
