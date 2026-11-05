plugins {
    id("audiosaas.android.library.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "dev.alnoer.audiosaas.feature.auth"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField(
            "String",
            "KEYCLOAK_BASE_URL",
            "\"${providers.gradleProperty("KEYCLOAK_BASE_URL").get()}\"",
        )
        buildConfigField(
            "String",
            "KEYCLOAK_CLIENT_ID",
            "\"${providers.gradleProperty("KEYCLOAK_CLIENT_ID").get()}\"",
        )
        buildConfigField(
            "String",
            "KEYCLOAK_SCOPES",
            "\"${providers.gradleProperty("KEYCLOAK_SCOPES").get()}\"",
        )
        buildConfigField(
            "boolean",
            "KEYCLOAK_SKIP_ISSUER_HTTPS_CHECK",
            providers.gradleProperty("KEYCLOAK_SKIP_ISSUER_HTTPS_CHECK").get(),
        )
        buildConfigField(
            "String",
            "OAUTH_REDIRECT_URI",
            "\"${providers.gradleProperty("OAUTH_REDIRECT_URI").get()}\"",
        )
    }
}

dependencies {
    implementation(project(":core:auth"))
    implementation(project(":core:designsystem"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.appauth)
}
