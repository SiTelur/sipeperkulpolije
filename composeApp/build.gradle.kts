import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.internal.utils.getLocalProperty
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.3.0"
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    js(IR) {
        useEsModules()
        browser {
            commonWebpackConfig {
                cssSupport { enabled.set(true) }
            }
        }
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.components.resources)
            implementation(libs.ui.tooling.preview)
            implementation(libs.material3.adaptive.navigation.suite)
            implementation(libs.material.icons.extended)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.compose.navigation)
            implementation(libs.kotlinx.serialization.json)
            implementation(project.dependencies.platform(libs.supabase))
            implementation(libs.postgrest.kt)
            implementation(libs.ktor.client.core)
            implementation(libs.auth.kt)
            implementation(libs.koin.core)
            implementation(libs.koin.viewmodel)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(npm("xlsx", "0.18.5"))
        }

    }
}

android {
    namespace = "com.polije.sipeperpolije"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.polije.sipeperpolije"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        debugImplementation(libs.ui.tooling)
    }
}

buildkonfig {
    packageName = "com.polije.sipeperpolije"

    val apiBaseUrl = getLocalProperty("API_BASE_URL")
    val apiKey = getLocalProperty("API_KEY")

    defaultConfigs {
        buildConfigField(
            type = STRING,
            name = "API_BASE_URL",
            value = apiBaseUrl
        )

        buildConfigField(
            type = STRING,
            name = "API_KEY",
            value = apiKey
        )
    }

    targetConfigs {
        create("js") {
            buildConfigField(
                type = STRING,
                name = "API_BASE_URL",
                value = apiBaseUrl
            )

            buildConfigField(
                type = STRING,
                name = "API_KEY",
                value = apiKey
            )
        }
    }
}