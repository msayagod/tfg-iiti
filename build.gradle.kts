// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.hilt)              apply false
    alias(libs.plugins.ksp)               apply false
}


// Fuerza kotlinx-metadata en todas las configuraciones KAPT

allprojects {
    // aplica a todas las configuraciones whose name empieza por "kapt" o "ksp"
    configurations.matching {
        it.name.startsWith("kapt") || it.name.startsWith("ksp")
    }.configureEach {
        resolutionStrategy {
            // la 0.6.0 ya sabe leer Metadata 2.1.0
            force("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.0")
        }
    }
}