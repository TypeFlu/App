plugins {
    alias(libs.plugins.self.application)
    alias(libs.plugins.self.compose)
}

android {
    namespace = "me.typeflu.calculator"
    compileSdk = 36 // Updated from your initial build-logic, can be 34 if preferred

    defaultConfig {
        applicationId = "me.typeflu.calculator"
        minSdk = 24 // Your desired minSdk
        targetSdk = 36 // Updated from your initial build-logic, can be 34 if preferred
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // applicationDebuggable = true // This is true by default for debug builds
        }
    }

    // compileOptions and kotlinOptions are removed assuming they are handled by build-logic/convention plugins
    // based on your project structure (e.g., self.application plugin)

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlin.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.viewmodel.compose) // Added this line

    // Compose dependencies - without BOM, using direct versions from your TOML
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.kotlinx.coroutines.android)

}
