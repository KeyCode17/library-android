# ADR — Pinned Android toolchain & SDK levels

| | |
|---|---|
| Status | Accepted |
| Date | 2026-06-14 |
| Owner | _TODO: assign_ |

## Context
We need a stable, long-lived toolchain — not the newest preview — that supports the widest
device range without legacy build pain.

## Decision (as of 2026-06)
| Component | Pin | Why |
|---|---|---|
| IDE | Android Studio **Quail 1 stable (2026.1.1)** | promoted to Stable in early June 2026; Quail 2 (2026.1.2) is Canary — avoid |
| JDK | **17** | required by AGP 9.x; use Studio's embedded jbr-17 |
| AGP | **9.1.x** | current stable, mature (9.2 is newer; Quail 1 supports up to 9.2) |
| Gradle | **9.1** | required by AGP 9.x |
| SDK Build-Tools | **36.0.0** | matches AGP 9.1 |
| Kotlin (KGP) | **2.2.10** | bundled by AGP 9.1; used by the Compose compiler plugin + KSP |
| `compileSdk` / `targetSdk` | **36 (Android 16)** | latest stable API; max supported by AGP 9.1 |
| `minSdk` | **24 (Android 7.0)** | ~98% device coverage; no legacy multidex, default interface methods work |

## Built-in Kotlin (AGP 9)
AGP 9.x compiles Kotlin **natively**. The `org.jetbrains.kotlin.android` (kotlin-android)
plugin must **not** be applied — it is incompatible with the new DSL and fails the build.
The Compose compiler plugin (`org.jetbrains.kotlin.plugin.compose`) is still applied to
every module that uses Compose. See the bundled config templates in
`docs/plan/_config-templates/`.

## minSdk rationale
24 is the pragmatic floor: below 24 you hit multidex/desugaring friction and lose default
interface methods. 24 still reaches the vast majority of active devices. Drop to 23/21 only
if a concrete audience needs it, accepting the build trade-offs.

## Consequences
- Pin Gradle in `gradle-wrapper.properties`, AGP/Kotlin in the version catalog
  (`gradle/libs.versions.toml`), SDK levels in `app/build.gradle.kts`.
- No kotlin-android plugin anywhere; Compose compiler plugin stays.
- Revisit when AGP 10.0 lands (mid-2026) — it removes the 9.x compatibility opt-outs
  (including the `android.builtInKotlin=false` escape hatch).
