// app/build.gradle.kts — SDK levels pinned per ADR-0001
// AGP 9.x has built-in Kotlin: the kotlin-android plugin is REMOVED (applying it now
// fails the build). The Compose compiler plugin is still applied.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)   // still required for Jetpack Compose
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.library.android"
    compileSdk = 36                 // Android 16 — latest stable, max for AGP 9.1

    defaultConfig {
        applicationId = "com.library.android"
        minSdk = 24                 // Android 7.0 — wide coverage, no legacy multidex
        targetSdk = 36              // match compileSdk
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17   // AGP 9.x requires JDK 17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // AGP 9 registers the `kotlin` extension via built-in Kotlin:
    kotlin { jvmToolchain(17) }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    // implementation(libs.jna)  // uncomment at M2 for the recommender UniFFI binding
}
