# docs/designs

HTML design mockups — the **binding** visual spec for these screens. Per the build rules,
implement screens by slicing the actual markup here (layout, component structure, and the
design tokens in each file's `:root`), not from a summary.

Design system "Stacks": Fraunces (display) + Inter (body) + JetBrains Mono (call numbers).
Palette and spacing are CSS custom properties in `:root` — extract them verbatim.
Signature element: the **shelf-location tab** (`.shelf-tab`) — the app's rack·row book-finder.

Files: `catalog.html` (list), `catalog-detail.html` (book detail). Open in a browser to view.
