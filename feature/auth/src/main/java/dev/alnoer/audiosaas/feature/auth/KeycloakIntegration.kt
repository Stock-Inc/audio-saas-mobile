package dev.alnoer.audiosaas.feature.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
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

interface KeycloakIntegration {
    fun processResult(
        intent: Intent,
        onResult: (KeycloakAuthResult) -> Unit,
    )

    fun getLoginIntent(): Intent?
}

sealed interface KeycloakAuthResult {
    data class Success(val tokens: AuthTokens) : KeycloakAuthResult
    data class Failure(val message: String) : KeycloakAuthResult
}

@Singleton
class KeycloakIntegrationImpl @Inject constructor(
    @ApplicationContext applicationContext: Context
): KeycloakIntegration {
    private val keycloakBaseUrl = "http://10.0.2.2:9090/realms/demo"

    private val serviceConfiguration = AuthorizationServiceConfiguration(
        "$keycloakBaseUrl/protocol/openid-connect/auth".toUri(),
        "$keycloakBaseUrl/protocol/openid-connect/token".toUri(),
    )

    private val redirectUri = "dev.alnoer.audiosaas:/oauth2redirect".toUri()

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
            .setSkipIssuerHttpsCheck(true)
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
                        tokenError.message ?: "Token exchange failed",
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

    override fun getLoginIntent(): Intent? {
        try {
            val request = AuthorizationRequest.Builder(
                serviceConfiguration,
                "sonora-android",
                ResponseTypeValues.CODE,
                redirectUri
            )
                .setScope("openid profile email")
                .build()

            val intent = authService.getAuthorizationRequestIntent(request)

            return intent
        } catch (e: Exception) {
            Log.e("AUTH", "Cannot create login intent", e)
            return null
        }
    }
}
