pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// No JDK 17 is installed on this host (only JDK 21). The toolchain in app/build.gradle.kts
// pins Java 17 per ADR-0001; foojay auto-provisions a JDK 17 for compilation so the pin is
// honoured without weakening it. The Gradle daemon itself still runs on the host JDK.
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "library-android"
include(":app")
