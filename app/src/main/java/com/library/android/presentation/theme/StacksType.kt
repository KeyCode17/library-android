package com.library.android.presentation.theme

import androidx.compose.ui.text.font.FontFamily

/**
 * Type families for the Stacks design.
 *
 * Design spec (`docs/designs/catalog.html`): Display = Fraunces, Body = Inter,
 * Mono (call numbers / shelf labels) = JetBrains Mono. Those are Google Fonts; to avoid
 * bundling binary font assets (and a downloadable-fonts runtime/Play-services dependency that
 * would also make Robolectric tests flaky) M0/T-001 maps them to the matching generic
 * families that preserve the typographic intent (serif display, sans body, mono labels).
 * Swap to the real families once font assets / the downloadable-fonts provider land.
 */
object StacksType {
    val Display: FontFamily = FontFamily.Serif       // Fraunces
    val Body: FontFamily = FontFamily.SansSerif      // Inter
    val Mono: FontFamily = FontFamily.Monospace      // JetBrains Mono
}
