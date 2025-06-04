plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm()
    sourceSets {
        jvmMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)

                implementation(libs.jna)
                implementation(libs.jna.platform)
            }
        }
    }
}