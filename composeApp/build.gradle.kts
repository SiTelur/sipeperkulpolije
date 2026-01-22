import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.internal.utils.getLocalProperty
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
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

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.materialIconsExtended)
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

        val apiBaseUrl = getLocalProperty("API_BASE_URL")
        val apiKey = getLocalProperty("API_KEY")

        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
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
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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


dependencies {
    debugImplementation(compose.uiTooling)
}

