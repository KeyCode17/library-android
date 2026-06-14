package com.library.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.library.android.presentation.nav.AppNavHost
import com.library.android.presentation.theme.LibraryTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host. `@AndroidEntryPoint` lets composables resolve `@HiltViewModel`s via
 * `hiltViewModel()`. Navigation (catalog list → book detail) lives in [AppNavHost].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibraryTheme {
                AppNavHost()
            }
        }
    }
}
