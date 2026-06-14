# ADR — Adopt the Compose/Kotlin forbidden-patterns rule

| | |
|---|---|
| Status | Accepted |
| Date | 2026-06-14 |
| Owner | _TODO: assign_ |

## Context
Team comes from React/TanStack; certain Kotlin/Compose traps map to React anti-patterns.

## Decision
Adopt `compose-kotlin-forbidden-patterns.md` as an enforced rule (detekt + compose-rules +
Android Lint in CI). Banned: side effects in composable body, `GlobalScope`, `runBlocking`
on main, `!!`, public `MutableStateFlow`, ViewModel forwarding, data calls in composables.

## Consequences
- CI blocks the listed patterns (rule 9 — premature abstraction — is reviewer judgment).
