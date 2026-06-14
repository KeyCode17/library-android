// app/build.gradle.kts — SDK levels pinned per ADR-0001
// AGP 9.x has built-in Kotlin: the kotlin-android plugin is REMOVED (applying it now
// fails the build). The Compose compiler plugin is still applied.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)   // still required for Jetpack Compose
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.library.android"
    compileSdk = 36                 // Android 16 — latest stable, max for AGP 9.1
    buildToolsVersion = "36.1.0"    // the build-tools installed on this host

    defaultConfig {
        applicationId = "com.library.android"
        minSdk = 24                 // Android 7.0 — wide coverage, no legacy multidex
        targetSdk = 36              // match compileSdk
        versionCode = 2
        versionName = "0.2.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17   // AGP 9.x requires JDK 17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // AGP 9 registers the `kotlin` extension via built-in Kotlin:
    kotlin { jvmToolchain(17) }

    // Robolectric needs Android resources on the JVM unit-test classpath so the
    // pre-push gate can run Compose UI tests without an emulator.
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    lint {
        abortOnError = true        // a lint error fails the build (CI gate)
        warningsAsErrors = false
        checkReleaseBuilds = false // the gate runs lintDebug only
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    // implementation(libs.jna)  // uncomment at M2 for the recommender UniFFI binding

    // Networking (REST consumer of the backend contract) — no Room cache in this slice
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.security.crypto)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // JVM unit tests (Robolectric-backed Compose UI tests run here for the pre-push gate)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.ui.test.junit4)

    // Reserved for CI (connectedAndroidTest / Gradle Managed Devices), not the JVM gate
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // detekt Compose ruleset (compose-rules) — enforces the forbidden-patterns rule
    detektPlugins(libs.detekt.compose.rules)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    // Forbidden-patterns enforcement targets production code; JVM tests are gated separately.
    source.setFrom(files("src/main/java"))
}
