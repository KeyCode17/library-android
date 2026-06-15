package com.library.android.presentation.screens.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.library.android.domain.model.Principal
import com.library.android.domain.model.Role
import com.library.android.presentation.theme.LibraryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Compose UI tests for the auth screens (JVM/Robolectric, pre-push gate): the login form
 * renders, a 401 surfaces its error, the authenticated profile shows the principal, and the
 * signed-out profile shows the login gate.
 */
@RunWith(RobolectricTestRunner::class)
class AuthScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun login_formRenders() {
        composeRule.setContent {
            LibraryTheme { LoginContent(AuthUiState.Anonymous, onSubmit = { _, _ -> }) }
        }

        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Log in").assertIsDisplayed()
    }

    @Test
    fun login_showsError() {
        composeRule.setContent {
            LibraryTheme {
                LoginContent(AuthUiState.Error("Invalid email or password"), onSubmit = { _, _ -> })
            }
        }

        composeRule.onNodeWithText("Invalid email or password").assertIsDisplayed()
    }

    @Test
    fun profile_authenticated_showsPrincipal() {
        val principal = Principal("1", "dana@stacks.app", Role.MEMBER, verified = true, active = true)
        composeRule.setContent {
            LibraryTheme { ProfileContent(AuthUiState.Authenticated(principal)) }
        }

        composeRule.onNodeWithText("dana@stacks.app").assertIsDisplayed()
        composeRule.onNodeWithText("Log out").assertIsDisplayed()
    }

    @Test
    fun profile_signedOut_showsLoginGate() {
        composeRule.setContent {
            LibraryTheme { ProfileContent(AuthUiState.Anonymous) }
        }

        composeRule.onNodeWithText("You're signed out").assertIsDisplayed()
        composeRule.onNodeWithText("Log in").assertIsDisplayed()
    }
}
