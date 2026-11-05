package dev.alnoer.audiosaas.auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.alnoer.audiosaas.auth.data.AuthSessionRepositoryImpl
import dev.alnoer.audiosaas.auth.repository.AccessTokenProvider
import dev.alnoer.audiosaas.auth.repository.AuthSessionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthCoreModule {

    @Binds
    @Singleton
    abstract fun bindAuthSessionRepository(
        implementation: AuthSessionRepositoryImpl,
    ): AuthSessionRepository

    @Binds
    @Singleton
    abstract fun bindAccessTokenProvider(
        implementation: AuthSessionRepositoryImpl,
    ): AccessTokenProvider
}
