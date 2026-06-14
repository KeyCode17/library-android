package com.library.android.presentation.screens.reminders

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.library.android.domain.model.Reminder
import com.library.android.domain.model.ReminderKind
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Compose UI tests for the reminders screen (JVM/Robolectric, fakes). */
@RunWith(RobolectricTestRunner::class)
class RemindersContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val reminders = listOf(
        Reminder("r1", "u1", "l1", ReminderKind.DUE_SOON, "Pale Fire is due soon.", "2026-06-20T09:00:00Z"),
        Reminder("r2", "u1", "l2", ReminderKind.OVERDUE, "Wayfinding is overdue.", "2026-06-18T09:00:00Z"),
    )

    @Test
    fun rendersReminderHistory() {
        composeRule.setContent {
            LibraryTheme { RemindersContent(RemindersUiState(reminders = reminders)) }
        }

        composeRule.onNodeWithText("Pale Fire is due soon.").assertIsDisplayed()
        composeRule.onNodeWithText("Due soon").assertIsDisplayed()
        composeRule.onNodeWithText("Overdue").assertIsDisplayed()
    }

    @Test
    fun loading_displaysProgress() {
        composeRule.setContent {
            LibraryTheme { RemindersContent(RemindersUiState(isLoading = true)) }
        }

        composeRule.onNodeWithTag(RemindersTestTags.LOADING).assertIsDisplayed()
    }

    @Test
    fun anonymous_showsSignInGate() {
        composeRule.setContent {
            LibraryTheme { RemindersContent(RemindersUiState(isAnonymous = true)) }
        }

        composeRule.onNodeWithText("Sign in to see your reminders").assertIsDisplayed()
        composeRule.onNodeWithText("Log in").assertIsDisplayed()
    }
}
