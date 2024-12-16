import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            // implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        // buildTypes.release.proguard {
        //     optimize.set(false)
        // }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AdbHelper2.0"
            packageVersion = "1.0.1"
        }
    }
}

// task
tasks.register("buildApiDebug") {
    group = "build"
    doLast {
        val isDebug = project.hasProperty("debug")
        if (!isDebug) return@doLast // 如果不是 debug 模式，直接返回

        exec {
            workingDir = file("../api/adb_helper_api_rs")
            commandLine = listOf("cargo", "build", "-p", "api-jni")
        }
    }

}

tasks.register("buildApiRelease") {
    group = "build"
    doLast {
        val isDebug = project.hasProperty("debug")
        if (isDebug) return@doLast // 如果是 debug 模式，直接返回

        exec {
            workingDir = file("../api/adb_helper_api_rs")
            commandLine = listOf("cargo", "build", "-p", "api-jni", "--release")
        }
    }
}

tasks.register<Copy>("copyApiRelease") {
    val appDir = layout.buildDirectory.dir("compose/binaries/main/app").get()
    val appName = compose.desktop.application.nativeDistributions.packageName
    // println("appDir: $appDir")
    // println("appName: $appName")

    val os = System.getProperty("os.name").lowercase()
    if (os.contains("mac")) {
        from(file("../api/adb_helper_api_rs/target/release/libapi_jni.dylib"))
        into(file(appDir.dir("$appName.app/Contents/app/libs")))
    } else if (os.contains("windows")) {
        from(file("../api/adb_helper_api_rs/target/release/api_jni.dll"))
        into(file(appDir.dir("$appName/app/libs")))
    } else if (os.contains("linux")) {
        // 待确定具体路径
        // from(file("../api/adb_helper_api_rs/target/release/libapi_jni.so"))
        // into(file(appDir.dir("$appName/app/libs")))
    }
}

tasks.register("generateBuildConfig") {
    group = "build"
    doLast {
        val isDebug = project.hasProperty("debug")
        val buildConfig = file("src/commonMain/kotlin/BuildConfig.kt")
        buildConfig.writeText(
            """
            package compose
            
            object BuildConfig {
                const val DEBUG = $isDebug
            }
            """.trimIndent()
        )
    }
}

afterEvaluate {
    tasks.named("generateComposeResClass") {
        finalizedBy("generateBuildConfig")
    }

    tasks.named("compileKotlinDesktop") {
        finalizedBy("buildApiDebug", "buildApiRelease")
    }

    tasks.named("createDistributable") {
        finalizedBy("copyApiRelease")
    }
}
