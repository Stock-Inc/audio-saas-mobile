package dev.alnoer.audiosaas.feature.auth.data.remote

internal fun tokenExchangeFailureMessage(reason: String?): String = when (reason) {
    "ID Token expired",
    "Issued at time is more than 10 minutes before or after the current time" ->
        "Проверьте автоматическую дату и время на устройстве и сервере Keycloak, затем повторите вход."
    "Issuer mismatch" ->
        "Адрес issuer в Keycloak не совпадает с discovery. Проверьте настройки адреса Keycloak."
    "Issuer must be an https URL" ->
        "Для входа требуется HTTPS. Локальный HTTP разрешайте только в настройках debug-сборки."
    "Audience mismatch" ->
        "ID token выдан другому клиенту. Проверьте Client ID в Keycloak и настройках приложения."
    "Nonce mismatch" ->
        "Не удалось подтвердить запрос входа. Закройте окно авторизации и начните вход заново."
    else -> "Не удалось получить токены. Повторите вход."
}
