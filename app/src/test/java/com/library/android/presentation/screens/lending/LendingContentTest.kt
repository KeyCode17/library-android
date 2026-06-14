package com.library.android.presentation.screens.lending

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.library.android.domain.model.Loan
import com.library.android.domain.model.LoanStatus
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Compose UI tests for the lending screen (JVM/Robolectric): loans render with status, the
 * approve action is staff-only, and the signed-out gate shows. Role is driven via the state.
 */
@RunWith(RobolectricTestRunner::class)
class LendingContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val borrowedLoan =
        Loan("l1", "b1", "u1", LoanStatus.BORROWED, "2026-06-01T10:00:00Z", "2026-06-15T10:00:00Z")
    private val returnedLoan =
        Loan("l2", "b2", "u2", LoanStatus.RETURNED, "2026-05-01T10:00:00Z", "2026-05-15T10:00:00Z")

    @Test
    fun myLoans_renderStatusAndActions() {
        composeRule.setContent {
            LibraryTheme { LendingContent(LendingUiState(loans = listOf(borrowedLoan), isStaff = false)) }
        }

        composeRule.onNodeWithText("Borrowed").assertIsDisplayed()
        composeRule.onNodeWithText("Return").assertIsDisplayed()
        composeRule.onNodeWithText("Scan to borrow").assertIsDisplayed()
    }

    @Test
    fun approve_shownForStaff() {
        composeRule.setContent {
            LibraryTheme { LendingContent(LendingUiState(loans = listOf(returnedLoan), isStaff = true)) }
        }

        composeRule.onNodeWithText("Approve").assertIsDisplayed()
    }

    @Test
    fun approve_hiddenForMembers() {
        composeRule.setContent {
            LibraryTheme { LendingContent(LendingUiState(loans = listOf(returnedLoan), isStaff = false)) }
        }

        composeRule.onNodeWithText("Approve").assertDoesNotExist()
    }

    @Test
    fun anonymous_showsLoginGate() {
        composeRule.setContent {
            LibraryTheme { LendingContent(LendingUiState(isAnonymous = true)) }
        }

        composeRule.onNodeWithText("Sign in to view your loans").assertIsDisplayed()
        composeRule.onNodeWithText("Log in").assertIsDisplayed()
    }
}
