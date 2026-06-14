# PLAN — Android Implementation

| | |
|---|---|
| Status | Draft |
| Date | 2026-06-14 |
| Owner | _TODO: assign_ |

## Milestones
1. **M0 — Environment + shell.** Toolchain pinned (ADR-0001), app skeleton, theme, nav,
   Hilt, Room scaffolding. See plan/002 for setup.
2. **M1 — Catalog + Lending UI.** Screens over REST + Room cache.
3. **M2 — Recommender binding.** Consume AAR from `library-backend/build.sh`.
4. **M3 — Chat.** WebSocket client + UI.
5. **M4 — Notifications.** FCM + reminder UX.
6. **M5 — Scan + WiFi + QR card.** ML Kit + platform APIs.

## Dependencies
- M2 blocked on backend publishing the recommender AAR.
- Chat blocked on backend WebSocket endpoint + contract.
