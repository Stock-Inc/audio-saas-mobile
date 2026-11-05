plugins {
    id("audiosaas.android.feature")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "dev.alnoer.audiosaas.feature.upload"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val uploadUrl = providers.gradleProperty("AUDIO_UPLOAD_URL").orElse("").get()
        buildConfigField("String", "UPLOAD_URL", "\"$uploadUrl\"")
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:network"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.ktor.client.okhttp)

    testImplementation(libs.junit)
}
