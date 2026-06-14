# SOW — Android App Initial Scope

| | |
|---|---|
| Status | Draft |
| Date | 2026-06-14 |
| Owner | _TODO: assign_ |

## 1. Objective
Native Android client for the library platform: catalog browsing, borrowing/returning,
barcode scan, chat, reminders, and on-device recommendations.

## 2. In scope
- Compose UI (Material 3) for catalog, lending, chat, profile, recommendations.
- Local data via **Room** (offline cache + local CRUD).
- REST integration with the backend for shared/multi-user data.
- On-device recommendations via the `recommender` UniFFI binding.
- Barcode/QR scan (ML Kit), WiFi provisioning (platform APIs).

## 3. Out of scope
- Business logic that belongs server-side (authoritative auth, licensing).
- Any Rust logic beyond consuming the recommender binding (ADR-0003).
- iOS app (separate team/repo).

## 4. Deliverables
- Installable app with the feature set above.
- Documented, reproducible dev environment (see plan/002).

## 5. Acceptance
- Builds on the pinned toolchain (ADR-0001) from a clean checkout.
- CRUD works offline via Room; recommendations run on-device.
