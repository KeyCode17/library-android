package com.library.android.presentation.screens.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.library.android.domain.model.Book
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Compose UI tests for every [BookDetailUiState] (JVM/Robolectric, pre-push gate). Drives the
 * stateless [BookDetailContent] directly.
 */
@RunWith(RobolectricTestRunner::class)
class BookDetailContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val book =
        Book("1", "The Left Hand of Darkness", "Ursula K. Le Guin", "9780441478125", "R12", 3, true)

    @Test
    fun content_displaysBookFields() {
        composeRule.setContent {
            LibraryTheme { BookDetailContent(BookDetailUiState.Content(book)) }
        }

        // Title + author each render twice (cover + hero), so match the first.
        composeRule.onAllNodesWithText("The Left Hand of Darkness").onFirst().assertIsDisplayed()
        composeRule.onAllNodesWithText("Ursula K. Le Guin").onFirst().assertIsDisplayed()
        composeRule.onNodeWithText("R12 · Row 3").assertIsDisplayed()
        composeRule.onNodeWithText("ISBN 9780441478125").assertIsDisplayed()
        composeRule.onNodeWithText("Borrow").assertIsDisplayed()
    }

    @Test
    fun loading_displaysProgress() {
        composeRule.setContent {
            LibraryTheme { BookDetailContent(BookDetailUiState.Loading) }
        }

        composeRule.onNodeWithTag(BookDetailTestTags.LOADING).assertIsDisplayed()
    }

    @Test
    fun notFound_displaysMessage() {
        composeRule.setContent {
            LibraryTheme { BookDetailContent(BookDetailUiState.NotFound) }
        }

        composeRule.onNodeWithText("Book not found").assertIsDisplayed()
    }

    @Test
    fun error_displaysMessageAndRetry() {
        composeRule.setContent {
            LibraryTheme { BookDetailContent(BookDetailUiState.Error("Couldn't reach the server")) }
        }

        composeRule.onNodeWithText("Couldn't reach the server").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}
