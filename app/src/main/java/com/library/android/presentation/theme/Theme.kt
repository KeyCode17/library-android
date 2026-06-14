package com.library.android.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Stacks is a light, paper-toned design; a single light scheme mapped from the design tokens.
private val StacksColorScheme = lightColorScheme(
    primary = StacksColors.Pine,
    onPrimary = StacksColors.OnAccent,
    background = StacksColors.Bg,
    onBackground = StacksColors.Ink,
    surface = StacksColors.Surface,
    onSurface = StacksColors.Ink,
    outline = StacksColors.Line,
)

/**
 * App Material 3 theme, mapped from the Stacks design tokens ([StacksColors]). Components that
 * need exact design colours read [StacksColors] directly; the scheme covers Material defaults.
 */
@Composable
fun LibraryTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = StacksColorScheme,
        content = content,
    )
}
