package dev.alnoer.audiosaas.feature.auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.alnoer.audiosaas.feature.auth.data.remote.KeycloakIntegration
import dev.alnoer.audiosaas.feature.auth.data.remote.KeycloakIntegrationImpl
import dev.alnoer.audiosaas.feature.auth.data.repository.AuthRepositoryImpl
import dev.alnoer.audiosaas.feature.auth.domain.repository.AuthRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun bindKeycloakIntegration(
        implementation: KeycloakIntegrationImpl,
    ): KeycloakIntegration

    @Binds
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl,
    ): AuthRepository
}
