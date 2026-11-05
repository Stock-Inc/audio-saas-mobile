plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.android.tools.build:gradle:${libs.versions.agp.get()}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
}

gradlePlugin {
    plugins {
        register("androidComposeLibrary") {
            id = "audiosaas.android.library.compose"
            implementationClass = "AndroidComposeLibraryConventionPlugin"
        }
        register("androidLibrary") {
            id = "audiosaas.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}
