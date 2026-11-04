plugins {
    id("audiosaas.android.feature")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "dev.alnoer.audiosaas.feature.auth"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:local"))
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
