package dev.alnoer.audiosaas.feature.auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.alnoer.audiosaas.feature.auth.AuthRepository
import dev.alnoer.audiosaas.feature.auth.AuthRepositoryImpl
import dev.alnoer.audiosaas.feature.auth.KeycloakIntegration
import dev.alnoer.audiosaas.feature.auth.KeycloakIntegrationImpl
import dev.alnoer.audiosaas.feature.auth.LocalAuthRepository
import dev.alnoer.audiosaas.feature.auth.LocalAuthRepositoryImpl

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
