package com.library.android.presentation.screens.admin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.library.android.domain.model.Role
import com.library.android.domain.model.UserSummary
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Compose UI tests (JVM/Robolectric) for the admin Manage Users screen. */
@RunWith(RobolectricTestRunner::class)
class ManageUsersContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val users = listOf(
        UserSummary("1", "ada@stacks.app", Role.ADMIN, verified = true, active = true, createdAt = "t"),
        UserSummary("2", "mem@stacks.app", Role.MEMBER, verified = false, active = false, createdAt = "t"),
    )

    @Test
    fun list_rendersUsersAndCreateForm() {
        composeRule.setContent {
            LibraryTheme {
                ManageUsersContent(ManageUsersUiState(isLoading = false, users = users))
            }
        }

        composeRule.onNodeWithText("New user").assertIsDisplayed()
        composeRule.onNodeWithText("ada@stacks.app").assertIsDisplayed()
        composeRule.onNodeWithText("mem@stacks.app").assertIsDisplayed()
    }

    @Test
    fun nonAdmin_seesForbiddenMessage() {
        composeRule.setContent {
            LibraryTheme {
                ManageUsersContent(ManageUsersUiState(isLoading = false, isForbidden = true))
            }
        }

        composeRule.onNodeWithText("Admin access required").assertIsDisplayed()
    }
}
