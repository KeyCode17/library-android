package com.library.android.presentation.screens.recommend

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
 * Compose UI tests for the recommendations screen (JVM/Robolectric, with the fake recommender
 * driving state): ranked results render, plus the loading, empty and idle states.
 */
@RunWith(RobolectricTestRunner::class)
class RecommendationsContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val ranked = listOf(
        Book("b", "Bravo", "Author B", "isbn-b", "R12", 2, true),
        Book("a", "Alpha", "Author A", "isbn-a", "R01", 1, true),
    )

    @Test
    fun loaded_rendersRankedBooks() {
        composeRule.setContent {
            LibraryTheme { RecommendationsContent(RecommendationsUiState.Loaded(ranked)) }
        }

        // Title renders on the cover and the row, so match the first; the form button is present.
        composeRule.onAllNodesWithText("Bravo").onFirst().assertIsDisplayed()
        composeRule.onNodeWithText("Recommend").assertIsDisplayed()
    }

    @Test
    fun loading_displaysProgress() {
        composeRule.setContent {
            LibraryTheme { RecommendationsContent(RecommendationsUiState.Loading) }
        }

        composeRule.onNodeWithTag(RecommendTestTags.LOADING).assertIsDisplayed()
    }

    @Test
    fun empty_displaysMessage() {
        composeRule.setContent {
            LibraryTheme { RecommendationsContent(RecommendationsUiState.Empty) }
        }

        composeRule.onNodeWithText("No matches — try different preferences.").assertIsDisplayed()
    }

    @Test
    fun idle_displaysPrompt() {
        composeRule.setContent {
            LibraryTheme { RecommendationsContent(RecommendationsUiState.Idle) }
        }

        composeRule.onNodeWithText("Set your preferences, then tap Recommend.").assertIsDisplayed()
    }
}
