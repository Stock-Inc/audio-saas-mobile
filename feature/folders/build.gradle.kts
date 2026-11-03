plugins {
    id("audiosaas.android.feature")
}

android {
    namespace = "dev.alnoer.audiosaas.feature.folders"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
}
