package com.library.android.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.library.android.presentation.theme.LibraryTheme

/**
 * Placeholder home screen for M0. Follows the Screen/Content split from the
 * android-kotlin-compose skill: [HomeScreen] is the (future) stateful container,
 * [HomeContent] is the pure, previewable UI. The catalog screen replaces this at T-001a
 * once the backend contract lands.
 */
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    HomeContent(modifier = modifier)
}

@Composable
fun HomeContent(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Library",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    LibraryTheme {
        HomeContent()
    }
}
