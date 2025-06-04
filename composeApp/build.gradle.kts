import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.desktop.application.tasks.AbstractJPackageTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    // alias(libs.plugins.compose.hot.reload)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            // implementation(libs.material.icons)
            // implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(project(":window-styler"))
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.adbhelper.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AdbHelper2.0"
            packageVersion = "1.0.1"
        }

        buildTypes {
            release {
                proguard {
                    configurationFiles.from(file("proguard.pro"))
                    optimize = false
                }
            }
        }
    }
}

// Run
tasks.withType(JavaExec::class.java) {
    doFirst {
        exec {
            workingDir = file("../api/adb_helper_api_rs")
            commandLine = listOf("cargo", "build", "-p", "api-jni")
        }
    }
}

// Package
tasks.withType(AbstractJPackageTask::class.java) {
    doFirst {
        exec {
            workingDir = file("../api/adb_helper_api_rs")
            commandLine = listOf("cargo", "build", "-p", "api-jni", "--release")
        }
    }

    doLast {
        val abstractJPackageTask = this@withType
        val destinationDir = abstractJPackageTask.destinationDir.orNull
        val appName = abstractJPackageTask.packageName.orNull

        require(destinationDir != null)
        require(appName != null)

        copy {
            val os = System.getProperty("os.name").lowercase()
            var fromFile: File? = null
            var intoFile: File? = null

            if (os.contains("mac")) {
                fromFile = file("../api/adb_helper_api_rs/target/release/libapi_jni.dylib")
                intoFile = file(destinationDir.dir("$appName.app/Contents/app/libs"))
            } else if (os.contains("windows")) {
                fromFile = file("../api/adb_helper_api_rs/target/release/api_jni.dll")
                intoFile = file(destinationDir.dir("$appName/app/libs"))
            } else if (os.contains("linux")) {
                // 待确定具体路径
                // fromFile = file("../api/adb_helper_api_rs/target/release/libapi_jni.so")
                // intoFile = file(destinationDir.dir("$appName/app/libs"))
            }

            require(fromFile != null)
            require(intoFile != null)

            if (!fromFile.exists()) error("`$fromFile` not found!")

            println("The lib to copy the action: $fromFile -> $intoFile")
            from(fromFile)
            into(intoFile)
        }
    }
}
