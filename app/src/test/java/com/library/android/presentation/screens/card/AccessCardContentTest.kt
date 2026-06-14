package com.library.android.presentation.screens.card

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.library.android.domain.device.QrMatrix
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Compose UI tests for the access-card screen (JVM/Robolectric, fakes). */
@RunWith(RobolectricTestRunner::class)
class AccessCardContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val matrix = QrMatrix(2, booleanArrayOf(true, false, false, true))

    @Test
    fun ready_rendersQrAndMember() {
        composeRule.setContent {
            LibraryTheme {
                AccessCardContent(AccessCardUiState.Ready("u1", "dana@stacks.app", matrix))
            }
        }

        composeRule.onNodeWithTag(AccessCardTestTags.QR).assertIsDisplayed()
        composeRule.onNodeWithText("dana@stacks.app").assertIsDisplayed()
        composeRule.onNodeWithText("ID u1").assertIsDisplayed()
    }

    @Test
    fun anonymous_showsSignInGate() {
        composeRule.setContent {
            LibraryTheme { AccessCardContent(AccessCardUiState.Anonymous) }
        }

        composeRule.onNodeWithText("Sign in to show your access card").assertIsDisplayed()
    }
}
