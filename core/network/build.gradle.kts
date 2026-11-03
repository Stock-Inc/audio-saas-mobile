plugins {
    id("audiosaas.android.core")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "dev.alnoer.audiosaas.core.network"
}

dependencies {
    implementation(libs.ktor.client.okhttp)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
