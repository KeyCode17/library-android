# FSD — Android Behaviour

| | |
|---|---|
| Status | Draft |
| Date | 2026-06-14 |
| Owner | _TODO: assign_ |

## 1. Architecture
Clean layering + Unidirectional Data Flow. `presentation` (Compose + ViewModel) →
`domain` (model + use case + repo interface) → `data` (Room impl + REST + recommender
binding). See ADR-0004 and the forbidden-patterns rule.

## 2. Data ownership
- CRUD & offline cache: **Room** (Kotlin), behind repository interfaces.
- Shared/multi-user data: REST to backend.
- Recommendations: `recommender` UniFFI binding, entered at the data layer behind a
  repository (the rest of the app doesn't know Rust exists).

## 3. State
`UiState` data class per screen, exposed as `StateFlow` from a `@HiltViewModel`.
State down, events up. Side effects in `LaunchedEffect`. No React-style hook traps —
see `compose-kotlin-forbidden-patterns`.

## 4. Device features
- Barcode/QR: ML Kit (native), result posted to `lending`.
- WiFi provisioning: `WifiNetworkSuggestion` / WiFi Easy Connect (DPP) — platform APIs.

## 5. Navigation
Navigation Compose; routes as `object Routes` constants; auth/role gates in `beforeLoad`.
