package com.library.android.presentation.screens.wifi

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Compose UI tests for the WiFi provisioning screen (JVM/Robolectric, state-driven). */
@RunWith(RobolectricTestRunner::class)
class WifiContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun idle_showsNetworkAndConnect() {
        composeRule.setContent {
            LibraryTheme { WifiContent(WifiUiState.Idle, networkName = "Stacks Library") }
        }

        composeRule.onNodeWithText("Stacks Library").assertIsDisplayed()
        composeRule.onNodeWithText("Connect").assertIsDisplayed()
    }

    @Test
    fun success_showsConfirmation() {
        composeRule.setContent {
            LibraryTheme { WifiContent(WifiUiState.Success) }
        }

        composeRule.onNodeWithText("Network added — your device may connect automatically.")
            .assertIsDisplayed()
    }

    @Test
    fun unsupported_showsGuidance() {
        composeRule.setContent {
            LibraryTheme { WifiContent(WifiUiState.Unsupported) }
        }

        composeRule.onNodeWithText("WiFi provisioning needs Android 10 or newer.").assertIsDisplayed()
    }
}
