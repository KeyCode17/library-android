# docs/designs

HTML design mockups — the **binding** visual spec for these screens. Per the build rules,
implement screens by slicing the actual markup here (layout, component structure, and the
design tokens in each file's `:root`), not from a summary.

Design system "Stacks": Fraunces (display) + Inter (body) + JetBrains Mono (call numbers).
Palette and spacing are CSS custom properties in `:root` — extract them verbatim.
Signature element: the **shelf-location tab** (`.shelf-tab`) — the app's rack·row book-finder.

## Files

`kit.css` is the shared, canonical design kit (tokens + component classes) extracted from
`catalog.html`/`catalog-detail.html`; every other mockup links it so the whole set reads as one
system. Open any file in a browser to view.

- `catalog.html`, `catalog-detail.html` — list + book detail (original, self-contained spec)
- `kit.css` — shared tokens + components (link target for the screens below)
- Auth: `login.html`, `register.html`, `profile.html`, `forgot-password.html`,
  `reset-password.html`, `verify-email.html`
- Account / admin: `account.html`, `manage-users.html`
- Features: `lending.html`, `recommend.html`, `chat.html`, `reminders.html`,
  `access-card.html`, `wifi.html`
