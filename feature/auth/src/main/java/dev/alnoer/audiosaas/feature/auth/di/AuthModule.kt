package dev.alnoer.audiosaas.feature.auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.alnoer.audiosaas.feature.auth.data.remote.KeycloakIntegration
import dev.alnoer.audiosaas.feature.auth.data.remote.KeycloakIntegrationImpl
import dev.alnoer.audiosaas.feature.auth.data.repository.AuthRepositoryImpl
import dev.alnoer.audiosaas.feature.auth.data.repository.LocalAuthRepositoryImpl
import dev.alnoer.audiosaas.feature.auth.domain.repository.AuthRepository
import dev.alnoer.audiosaas.feature.auth.domain.repository.LocalAuthRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindKeycloakIntegration(
        keycloakIntegrationImpl: KeycloakIntegrationImpl,
    ): KeycloakIntegration

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    abstract fun bindLocalAuthRepository(
        localAuthRepositoryImpl: LocalAuthRepositoryImpl,
    ): LocalAuthRepository
}
