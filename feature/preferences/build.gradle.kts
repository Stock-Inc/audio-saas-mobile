plugins {
    id("audiosaas.android.core")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "dev.alnoer.audiosaas.feature.preferences"
}

dependencies {
    implementation(project(":core:local"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
