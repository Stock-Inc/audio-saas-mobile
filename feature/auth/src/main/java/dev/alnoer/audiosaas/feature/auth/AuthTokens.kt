package dev.alnoer.audiosaas.feature.auth

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)
