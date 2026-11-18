plugins {
    id("audiosaas.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "dev.alnoer.audiosaas.core.library"
    buildFeatures { buildConfig = true }
    defaultConfig {
        buildConfigField("String", "API_BASE_URL", "\"${providers.gradleProperty("AUDIO_API_BASE_URL").get()}\"")
    }
}

dependencies {
    implementation(project(":core:auth"))
    implementation(project(":core:network"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    testImplementation(libs.ktor.client.mock)
}
