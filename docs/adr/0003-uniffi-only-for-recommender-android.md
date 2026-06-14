# ADR — UniFFI only for the recommender

| | |
|---|---|
| Status | Accepted |
| Date | 2026-06-14 |
| Owner | _TODO: assign_ |

## Context
UniFFI earns its place only for heavy/shared compute, not CRUD.

## Decision
The only Rust the app touches is the `recommender` binding, entered at the data layer
behind a repository. Everything else is Kotlin/Room/REST.

## Consequences
- The app stays Rust-agnostic except one data source.
- If recommendations stay a light decision tree and never need to be offline-identical with
  iOS, even this could be plain Kotlin — keep the binding only while it earns its keep.
