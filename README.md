# library-android

Native Android client for the Library project (Kotlin + Jetpack Compose).

**Version:** 0.1.0 · **Status:** catalog list (T-001) on the M0 toolchain.

## What's built

A Compose app that launches to the **catalog list** screen (`docs/designs/catalog.html`),
fetching books from the backend's `GET /books` over REST. Lending, chat, recommender, etc.
arrive in later milestones (see `docs/plan/001-implementation-plan-android.md`).

- **UI:** Jetpack Compose + Material 3; catalog list with Loading / Content / Empty / Error states
- **Networking:** Retrofit + kotlinx.serialization; DTOs mirrored from
  `../library-backend/contract/openapi.yaml` (single source of truth). Base URL is the local
  gateway `http://10.0.2.2:8080/` (emulator → host loopback). No Room cache yet (fast-follow).
- **DI:** Hilt (network + repository modules)
- **Local data:** Room (deps wired; no entities yet)
- **Architecture:** MVVM / Unidirectional Data Flow (see `docs/adr/0004`) — `CatalogViewModel`
  exposes a single `StateFlow<CatalogUiState>`; `CatalogScreen` (stateful) + `CatalogContent`
  (stateless, previewable)
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
