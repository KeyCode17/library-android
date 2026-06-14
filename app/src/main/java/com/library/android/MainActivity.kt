package com.library.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.library.android.presentation.screens.home.HomeScreen
import com.library.android.presentation.theme.LibraryTheme

/**
 * Single-activity host. M0 has no injected dependencies, so it stays a plain
 * [ComponentActivity]; it becomes a Hilt `@AndroidEntryPoint` once a screen needs a ViewModel.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibraryTheme {
                HomeScreen()
            }
        }
    }
}
