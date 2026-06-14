# PRD — Android App

| | |
|---|---|
| Status | Draft |
| Date | 2026-06-14 |
| Owner | _TODO: assign_ |

## 1. Users
Members (browse/borrow on their phones) and librarians (scan-to-approve, manage).

## 2. Capabilities
- Browse catalog, find a book's shelf/row.
- Borrow/return; librarian approves via barcode scan.
- Group chat; ask-a-librarian.
- Borrowing reminders (push).
- Personalized recommendations (on-device, works offline).
- Library access QR card; WiFi onboarding.

## 3. Non-functional
- Works on a wide device range (see ADR-0001 for minSdk rationale).
- Recommendations are instant and offline-capable.
- Readable, previewable UI code (MVVM + UDF, ADR-0004).

## 4. Success metrics
- Cold start < 2s on mid-range devices.
- Recommendation render < 100ms on-device.
