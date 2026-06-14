# ADR — MVVM + Unidirectional Data Flow with Compose

| | |
|---|---|
| Status | Accepted |
| Date | 2026-06-14 |
| Owner | _TODO: assign_ |

## Context
First Kotlin codebase for the team; need a readable, consistent pattern.

## Decision
ViewModel + `StateFlow` + `UiState`; state down, events up. Screen/Content split
(stateful wrapper + stateless previewable content). Hilt DI. Use cases as
`operator fun invoke`. Navigation Compose.

## Consequences
- `@Preview` works on stateless content.
- Enforced by the `compose-kotlin-forbidden-patterns` rule (no side effects in composable
  bodies, no `GlobalScope`, no `!!`, no ViewModel forwarding, etc.).
