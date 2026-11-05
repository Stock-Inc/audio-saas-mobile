package dev.alnoer.audiosaas.auth.model

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAtMillis: Long,
) {
    fun isAccessTokenValid(nowMillis: Long = System.currentTimeMillis()): Boolean =
        accessTokenExpiresAtMillis > nowMillis
}
