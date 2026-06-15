package com.library.android.presentation.screens.catalog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Compose UI tests (JVM/Robolectric) for the wired catalog search field. */
@RunWith(RobolectricTestRunner::class)
class CatalogSearchTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun search_fieldRenders() {
        composeRule.setContent {
            LibraryTheme { CatalogContent(CatalogUiState.Content(sampleBooks)) }
        }

        composeRule.onNodeWithTag(CatalogTestTags.SEARCH_FIELD).assertIsDisplayed()
    }

    @Test
    fun search_imeActionSubmitsTheTrimmedQuery() {
        var captured: String? = null
        composeRule.setContent {
            LibraryTheme {
                CatalogContent(CatalogUiState.Content(sampleBooks), onSearch = { captured = it })
            }
        }

        composeRule.onNodeWithTag(CatalogTestTags.SEARCH_FIELD).performTextInput("  pale fire  ")
        composeRule.onNodeWithTag(CatalogTestTags.SEARCH_FIELD).performImeAction()

        assertEquals("pale fire", captured)
    }
}
