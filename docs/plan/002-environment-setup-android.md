# PLAN — Android Dev Environment Setup

| | |
|---|---|
| Status | Accepted |
| Date | 2026-06-14 |
| Owner | _TODO: assign_ |

This is the from-zero setup guide. Follow top to bottom; versions are pinned in ADR-0001.

## 1. Which IDE — Android Studio (not plain IntelliJ)
Use **Android Studio**. It is built on IntelliJ IDEA but is the purpose-built Android IDE:
it bundles the Android SDK manager, the AVD (emulator) manager, the Compose preview, and
the layout/profiling tools. Plain IntelliJ IDEA Ultimate *can* do Android via a plugin, but
you'd assemble those pieces yourself — not worth it. JetBrains Fleet is not for Android.

> Install the **stable** channel. As of mid-2026 that is **Android Studio Quail 1**
> (2026.1.1) — it was promoted from Canary to Stable in early June 2026. The current
> **Quail 2** (2026.1.2) is the Canary/preview branch — avoid it for this project. Quail 1
> supports AGP 7.1 through 9.2, so our AGP 9.1 pin is well within range.

Download: https://developer.android.com/studio (pick the stable build for your OS).

## 2. JDK
AGP 9.x requires **JDK 17**. Android Studio bundles a compatible JDK (JetBrains Runtime) —
use the embedded JDK (Settings → Build Tools → Gradle → Gradle JDK → "jbr-17"). Don't fight
it with a system JDK unless you have a reason.

> **AGP 9 built-in Kotlin:** AGP 9.x compiles Kotlin natively. Do **not** apply the
> `org.jetbrains.kotlin.android` plugin — it's incompatible with the new DSL and fails the
> build. Keep the Compose compiler plugin (`org.jetbrains.kotlin.plugin.compose`). The
> bundled config templates already reflect this.

## 3. First launch
1. Open Android Studio → "More Actions" → **SDK Manager**.
2. SDK Platforms tab: install **Android 16 (API 36)** — this is `compileSdk`/`targetSdk`.
   Also tick "Show Package Details" and install the **API 24** platform (for old-device
   testing) — this is `minSdk`.
3. SDK Tools tab: install **Android SDK Build-Tools 36.0.0**, **Android SDK Platform-Tools**,
   **Android Emulator**, and (Apple Silicon/Intel respectively) the matching **system images**.

## 4. The emulator (AVD)
1. "More Actions" → **Virtual Device Manager** → Create device.
2. Pick a phone profile (e.g. **Pixel 8**). Hardware acceleration:
   - Apple Silicon Macs: choose an **arm64-v8a** system image.
   - Intel/AMD: choose an **x86_64** image (enable VT-x/AMD-V / Hyper-V or KVM).
3. System image: **API 36 (Android 16), Google APIs** for daily dev.
4. Create a **second** AVD at **API 24** to sanity-check the oldest supported version.
5. Tip: an AVD with "Google Play" image lets you test Play services; "Google APIs" is fine
   for most dev.

## 5. Create / open the project
- New project → **Empty Activity (Compose)**.
- In the wizard set: **Minimum SDK = API 24**, **Build configuration language = Kotlin DSL
  (build.gradle.kts)**.
- Let the first Gradle sync finish (it downloads the Gradle distribution pinned in
  `gradle/wrapper/gradle-wrapper.properties`).

## 6. Verify
- `./gradlew :app:assembleDebug` succeeds from a clean checkout.
- App runs on both the API 36 and API 24 AVDs.
- Compose `@Preview` renders in the editor (split a stateless `XxxContent` to preview it).

## 7. Consuming the recommender binding (later, M2)
The Rust `recommender-ffi` is delivered as an **AAR** by `library-backend/build.sh`
(jniLibs + generated Kotlin, via JNA). Add the `jna` dependency and the AAR; consume it at
the data layer behind a repository. You never build Rust from this repo.

## 8. Common gotchas
- "Android Gradle plugin requires Java 17": your Gradle JDK is wrong — set it to jbr-17.
- Emulator won't boot on Apple Silicon: you picked an x86_64 image — recreate with arm64.
- Slow sync first time is normal (downloads SDK + Gradle); subsequent syncs are cached.
