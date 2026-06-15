package com.library.android.presentation.screens.account

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Compose UI tests (JVM/Robolectric) for account self-service. */
@RunWith(RobolectricTestRunner::class)
class AccountContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun account_rendersAllSelfServiceSections() {
        composeRule.setContent {
            LibraryTheme { AccountContent(AccountUiState(isLoading = false, email = "dana@stacks.app")) }
        }

        composeRule.onNodeWithText("Update email").assertIsDisplayed()
        composeRule.onNodeWithText("Current password").assertIsDisplayed()
        composeRule.onNodeWithText("Change password").assertIsDisplayed()
        composeRule.onNodeWithText("Delete account").assertIsDisplayed()
    }

    @Test
    fun account_showsResultMessage() {
        composeRule.setContent {
            LibraryTheme {
                AccountContent(AccountUiState(isLoading = false, email = "x", message = "Email updated"))
            }
        }

        composeRule.onNodeWithText("Email updated").assertIsDisplayed()
    }
}
