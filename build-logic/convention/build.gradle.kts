plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.android.tools.build:gradle:${libs.versions.agp.get()}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
}

gradlePlugin {
    plugins {
        register("androidFeature") {
            id = "audiosaas.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidCore") {
            id = "audiosaas.android.core"
            implementationClass = "AndroidCoreConventionPlugin"
        }
    }
}
