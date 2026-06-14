# ADR — Room (Kotlin) for data, not Rust

| | |
|---|---|
| Status | Accepted |
| Date | 2026-06-14 |
| Owner | _TODO: assign_ |

## Context
The Rust core exists; tempting to route all data through it via UniFFI.

## Decision
On-device data (CRUD + offline cache) uses **Room (Kotlin)**, behind repository interfaces.
Rust is not used for data.

## Consequences
- Mirrors the reference app's actual practice (data = Room, UniFFI reserved for heavy
  compute). Simpler, idiomatic, fewer moving parts.
