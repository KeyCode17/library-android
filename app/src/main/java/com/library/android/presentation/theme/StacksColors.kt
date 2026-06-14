package com.library.android.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Stacks design tokens — extracted verbatim from `docs/designs/catalog.html` `:root`.
 * Names mirror the CSS custom properties so the design and the code stay in lockstep.
 */
object StacksColors {
    val Ink = Color(0xFF20231F)        // --ink: primary text
    val Muted = Color(0xFF6B6F69)      // --muted: secondary text
    val Faint = Color(0xFF9A9A93)      // --faint: tertiary / unavailable
    val Bg = Color(0xFFF7F6F1)         // --bg: page (cool paper)
    val Surface = Color(0xFFFFFFFF)    // --surface: cards
    val Line = Color(0xFFE5E3DB)       // --line: hairline borders
    val Pine = Color(0xFF2C5949)       // --pine: primary accent
    val Pine700 = Color(0xFF1F4034)    // --pine-700: primary pressed
    val Sage100 = Color(0xFFE6EDE7)    // --sage-100: available chip bg
    val Brass = Color(0xFF9A7637)      // --brass: shelf-tab accent
    val Brass700 = Color(0xFF6F531F)   // --brass-700: shelf-tab text
    val Brass100 = Color(0xFFF0E7D4)   // --brass-100: shelf-tab bg
    val OutChipBg = Color(0xFFEFEEE9)  // .avail.out background
    val OnAccent = Color(0xFFFFFFFF)   // text/icon on pine

    /** Spine/cover fills used in the design mock, applied deterministically per book. */
    val CoverPalette = listOf(
        Color(0xFF2C5949),
        Color(0xFF6B3B52),
        Color(0xFF3A4A6B),
        Color(0xFF1F4034),
        Color(0xFF8A6730),
        Color(0xFF714B3A),
    )
}
