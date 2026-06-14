package com.library.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.library.android.presentation.screens.home.HomeScreen
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Compose UI smoke test running on the JVM under Robolectric — no emulator required, so it
 * runs in the pre-push gate. Verifies the app renders its M0 placeholder ("Library"), which
 * is the headless stand-in for "the app boots to its placeholder screen".
 */
@RunWith(RobolectricTestRunner::class)
class HomeScreenRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun placeholder_isDisplayed() {
        composeRule.setContent {
            LibraryTheme {
                HomeScreen()
            }
        }

        composeRule.onNodeWithText("Library").assertIsDisplayed()
    }
}
