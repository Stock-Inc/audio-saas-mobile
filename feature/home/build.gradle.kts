plugins {
    id("audiosaas.android.feature")
}

android {
    namespace = "dev.alnoer.audiosaas.feature.home"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":feature:recordings"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
}
