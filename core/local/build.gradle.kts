plugins {
    id("audiosaas.android.core")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "dev.alnoer.audiosaas.core.local"
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.room3.runtime)
    ksp(libs.androidx.room3.compiler)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
