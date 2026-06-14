package com.library.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.library.android.presentation.screens.catalog.CatalogScreen
import com.library.android.presentation.theme.LibraryTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host. `@AndroidEntryPoint` lets composables resolve `@HiltViewModel`s
 * (e.g. `CatalogViewModel`) via `hiltViewModel()`.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibraryTheme {
                CatalogScreen()
            }
        }
    }
}
