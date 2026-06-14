# library-android

Native Android client for the Library project (Kotlin + Jetpack Compose).

**Version:** 0.1.0 · **Status:** M0 — booting skeleton (no feature code yet).

## What's built (M0)

A booting Compose app with the toolchain, JVM test stack, and the pre-push quality gate
wired. The app launches to a single placeholder screen ("Library"). Catalog, lending, chat,
recommender, etc. arrive in later milestones (see `docs/plan/001-implementation-plan-android.md`).

- **UI:** Jetpack Compose + Material 3
- **DI:** Hilt (graph scaffolded; no injected screens yet)
- **Local data:** Room (deps wired; no entities yet)
- **Architecture:** MVVM / Unidirectional Data Flow (see `docs/adr/0004`)
- **Toolchain (ADR-0001):** AGP 9.1, Gradle 9.1, JDK 17 toolchain, Kotlin 2.2.10,
  compileSdk/targetSdk 36, minSdk 24, no `kotlin-android` plugin (AGP 9 built-in Kotlin)

## Requirements

- Android Studio Quail 1 (2026.1.1) stable — see `docs/plan/002-environment-setup-android.md`
- Android SDK: platform API 36, build-tools 36.x
- JDK: the build pins a **JDK 17 toolchain**. If no JDK 17 is installed, Gradle
  auto-provisions one (foojay resolver in `settings.gradle.kts`); the Gradle daemon itself
  runs on JDK 17+.

## Run

```bash
# Build the debug APK
./gradlew :app:assembleDebug

# Install + launch on a running emulator/device
./gradlew :app:installDebug
```

Or open the project in Android Studio and Run `app` on an API 36 (or API 24) AVD.

## Test

```bash
# JVM unit + Compose UI tests (Robolectric, no emulator) — this is the pre-push gate
./gradlew testDebugUnitTest

# Static analysis
./gradlew detekt        # detekt + compose-rules (enforces kotlin-forbidden-pattern)
./gradlew lintDebug     # Android Lint

# On-device instrumentation tests (CI / local device only)
./gradlew connectedDebugAndroidTest
```

## Quality gate (lefthook)

Git hooks are managed by [lefthook](https://lefthook.dev/) (`lefthook.yml`). Install once:

```bash
lefthook install
```

- **pre-commit:** `./gradlew detekt`
- **pre-push:** `./gradlew lintDebug` → `detekt` → `testDebugUnitTest` (piped)

Never bypass the hooks (`--no-verify` is forbidden). If a hook fails, fix the root cause.
