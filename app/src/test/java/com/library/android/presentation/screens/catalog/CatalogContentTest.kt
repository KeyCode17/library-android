package com.library.android.presentation.screens.catalog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Compose UI tests for every [CatalogUiState], run on the JVM under Robolectric (pre-push
 * gate, no emulator). Drives the stateless [CatalogContent] directly.
 */
@RunWith(RobolectricTestRunner::class)
class CatalogContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun content_displaysBooks() {
        composeRule.setContent {
            LibraryTheme { CatalogContent(CatalogUiState.Content(sampleBooks), onRetry = {}) }
        }

        // Title also appears on the cover, so match the first occurrence; author + shelf are
        // unique to the top row. Availability label repeats across rows -> match first.
        composeRule.onAllNodesWithText("The Left Hand of Darkness").onFirst().assertIsDisplayed()
        composeRule.onNodeWithText("Ursula K. Le Guin").assertIsDisplayed()
        composeRule.onNodeWithText("R12").assertIsDisplayed()
        composeRule.onAllNodesWithText("Available").onFirst().assertIsDisplayed()
        // a11y: bottom-nav icons carry content descriptions for TalkBack.
        composeRule.onNodeWithContentDescription("Profile").assertExists()
    }

    @Test
    fun appBar_anonymous_showsSignInNotFakeAvatar() {
        composeRule.setContent {
            LibraryTheme {
                CatalogContent(CatalogUiState.Content(sampleBooks), accountInitials = null)
            }
        }

        composeRule.onNodeWithText("Sign in").assertIsDisplayed()
        composeRule.onNodeWithText("DK").assertDoesNotExist() // no leftover mockup avatar
    }

    @Test
    fun appBar_authenticated_showsRealInitials() {
        composeRule.setContent {
            LibraryTheme {
                CatalogContent(CatalogUiState.Content(sampleBooks), accountInitials = "DA")
            }
        }

        composeRule.onNodeWithText("DA").assertIsDisplayed()
        composeRule.onNodeWithText("Sign in").assertDoesNotExist()
    }

    @Test
    fun loading_displaysProgress() {
        composeRule.setContent {
            LibraryTheme { CatalogContent(CatalogUiState.Loading, onRetry = {}) }
        }

        composeRule.onNodeWithTag(CatalogTestTags.LOADING).assertIsDisplayed()
    }

    @Test
    fun empty_displaysMessage() {
        composeRule.setContent {
            LibraryTheme { CatalogContent(CatalogUiState.Empty, onRetry = {}) }
        }

        composeRule.onNodeWithText("Nothing on the shelves yet").assertIsDisplayed()
    }

    @Test
    fun error_displaysMessageAndRetry() {
        composeRule.setContent {
            LibraryTheme { CatalogContent(CatalogUiState.Error("Couldn't reach the server"), onRetry = {}) }
        }

        composeRule.onNodeWithText("Couldn't reach the server").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}
