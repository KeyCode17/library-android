package com.library.android.presentation.screens.detail

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.library.android.domain.model.Book
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Compose UI tests (JVM/Robolectric) for the wired detail Borrow action. */
@RunWith(RobolectricTestRunner::class)
class BookDetailBorrowTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val available = Book("1", "Dune", "Frank Herbert", "9780441013593", "R07", 2, true)
    private val onLoan = available.copy(available = false)

    @Test
    fun borrow_availableBook_invokesOnBorrow() {
        var borrowed = false
        composeRule.setContent {
            LibraryTheme {
                BookDetailContent(BookDetailUiState.Content(available), onBorrow = { borrowed = true })
            }
        }

        composeRule.onNodeWithText("Borrow").assertHasClickAction()
        composeRule.onNodeWithText("Borrow").performClick()

        assertTrue(borrowed)
    }

    @Test
    fun borrow_unavailableBook_showsOnLoanDisabled() {
        composeRule.setContent {
            LibraryTheme { BookDetailContent(BookDetailUiState.Content(onLoan)) }
        }

        composeRule.onNodeWithText("On loan").assertIsDisplayed()
        composeRule.onNodeWithText("On loan").assertIsNotEnabled()
    }

    @Test
    fun borrow_message_isShown() {
        composeRule.setContent {
            LibraryTheme {
                BookDetailContent(
                    BookDetailUiState.Content(onLoan),
                    borrow = BorrowUiState(message = "Borrowed — find it on the shelf"),
                )
            }
        }

        composeRule.onNodeWithText("Borrowed — find it on the shelf").assertIsDisplayed()
    }
}
