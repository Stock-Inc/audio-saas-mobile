package dev.alnoer.audiosaas.feature.auth.data.remote

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.alnoer.audiosaas.auth.model.AuthTokens
import dev.alnoer.audiosaas.feature.auth.BuildConfig
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.connectivity.ConnectionBuilder
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine

interface KeycloakIntegration {
    fun processResult(
        intent: Intent,
        onResult: (KeycloakAuthResult) -> Unit,
    )

    suspend fun getLoginIntent(): Intent?
}

sealed interface KeycloakAuthResult {
    data class Success(val tokens: AuthTokens) : KeycloakAuthResult
    data class Failure(val message: String) : KeycloakAuthResult
}

@Singleton
class KeycloakIntegrationImpl @Inject constructor(
    @ApplicationContext applicationContext: Context,
): KeycloakIntegration {
    private val keycloakBaseUrl = BuildConfig.KEYCLOAK_BASE_URL.trimEnd('/')

    private suspend fun fetchConfiguration(): AuthorizationServiceConfiguration =
        suspendCancellableCoroutine { continuation ->
            AuthorizationServiceConfiguration.fetchFromIssuer(
                keycloakBaseUrl.toUri(),
                { configuration, error ->
                    if (continuation.isActive) {
                        if (configuration != null) {
                            if (configuration.discoveryDoc?.issuer != keycloakBaseUrl) {
                                continuation.resumeWithException(
                                    IllegalStateException("Keycloak discovery issuer differs from KEYCLOAK_BASE_URL"),
                                )
                            } else {
                                continuation.resume(configuration)
                            }
                        } else {
                            continuation.resumeWithException(
                                error ?: IllegalStateException("Keycloak discovery is unavailable"),
                            )
                        }
                    }
                },
                connectionBuilder,
            )
        }

    private val redirectUri = BuildConfig.OAUTH_REDIRECT_URI.toUri()

    private val authState = AuthState()

    private val connectionBuilder = ConnectionBuilder { uri ->
        (URL(uri.toString()).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000
            readTimeout = 10000
            instanceFollowRedirects = false
        }
    }

    private val authService by lazy {
        val appAuthConfig = AppAuthConfiguration.Builder()
            .setConnectionBuilder(connectionBuilder)
            .setSkipIssuerHttpsCheck(BuildConfig.KEYCLOAK_SKIP_ISSUER_HTTPS_CHECK)
            .build()

        AuthorizationService(applicationContext, appAuthConfig)
    }


    override fun processResult(
        intent: Intent,
        onResult: (KeycloakAuthResult) -> Unit
    ) {
        val response = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)

        authState.update(response, error)

        if (response == null) {
            onResult(
                KeycloakAuthResult.Failure(
                    error?.message ?: "Response was null"
                )
            )
            return
        }

        authService.performTokenRequest(
            response.createTokenExchangeRequest()
        ) { tokenResponse, tokenError ->

            authState.update(tokenResponse, tokenError)

            if (tokenError != null) {
                Log.e("AUTH", "Token exchange failed", tokenError)

                onResult(
                    KeycloakAuthResult.Failure(
                        tokenExchangeFailureMessage(tokenError.cause?.message),
                    ),
                )
                return@performTokenRequest
            }

            val accessToken = tokenResponse?.accessToken
            val refreshToken = tokenResponse?.refreshToken
            val accessTokenExpiresAtMillis = tokenResponse?.accessTokenExpirationTime

            if (accessToken.isNullOrBlank()) {
                onResult(
                    KeycloakAuthResult.Failure(
                        "Access token is missing"
                    )
                )
                return@performTokenRequest
            }

            if (refreshToken.isNullOrBlank()) {
                onResult(
                    KeycloakAuthResult.Failure(
                        "Refresh token is missing"
                    )
                )
                return@performTokenRequest
            }

            if (accessTokenExpiresAtMillis == null) {
                onResult(
                    KeycloakAuthResult.Failure(
                        "Access token expiration is missing"
                    )
                )
                return@performTokenRequest
            }

            onResult(
                KeycloakAuthResult.Success(
                    AuthTokens(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        accessTokenExpiresAtMillis = accessTokenExpiresAtMillis,
                    )
                )
            )
        }
    }

    override suspend fun getLoginIntent(): Intent? {
        try {
            val request = AuthorizationRequest.Builder(
                fetchConfiguration(),
                BuildConfig.KEYCLOAK_CLIENT_ID,
                ResponseTypeValues.CODE,
                redirectUri
            )
                .setScope(BuildConfig.KEYCLOAK_SCOPES)
                .build()

            val intent = authService.getAuthorizationRequestIntent(request)

            return intent
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: Exception) {
            Log.e("AUTH", "Cannot create login intent", e)
            return null
        }
    }
}
