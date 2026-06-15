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
 * Compose UI tests (JVM/Robolectric) for the public IAM flows and the auth-screen hooks that
 * reach them: the login "Forgot password?" link, the forgot/reset/verify screens, and the
 * profile's unverified-email banner.
 */
@RunWith(RobolectricTestRunner::class)
class IamFlowScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun login_offersForgotPassword() {
        composeRule.setContent {
            LibraryTheme { LoginContent(AuthUiState.Anonymous, onSubmit = { _, _ -> }) }
        }

        composeRule.onNodeWithText("Forgot password?").assertIsDisplayed()
    }

    @Test
    fun forgot_sentState_isNeutralAndOffersCodeEntry() {
        composeRule.setContent {
            LibraryTheme { ForgotPasswordContent(ForgotPasswordUiState.Sent) }
        }

        // Neutral wording — does not confirm whether the account exists.
        composeRule.onNodeWithText("If an account exists", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Enter reset code").assertIsDisplayed()
    }

    @Test
    fun forgot_idleState_showsTheForm() {
        composeRule.setContent {
            LibraryTheme { ForgotPasswordContent(ForgotPasswordUiState.Idle) }
        }

        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Send reset link").assertIsDisplayed()
    }

    @Test
    fun reset_rendersCodeAndPasswordFields() {
        composeRule.setContent {
            LibraryTheme { ResetPasswordContent(ResetPasswordUiState(), initialToken = "abc") }
        }

        composeRule.onNodeWithText("Reset code").assertIsDisplayed()
        composeRule.onNodeWithText("Reset password").assertIsDisplayed()
    }

    @Test
    fun verify_idleState_showsManualEntry() {
        composeRule.setContent {
            LibraryTheme { VerifyEmailContent(VerifyEmailUiState.Idle) }
        }

        composeRule.onNodeWithText("Verification code").assertIsDisplayed()
        composeRule.onNodeWithText("Verify").assertIsDisplayed()
    }

    @Test
    fun verify_verifiedState_confirms() {
        composeRule.setContent {
            LibraryTheme { VerifyEmailContent(VerifyEmailUiState.Verified) }
        }

        composeRule.onNodeWithText("Your email is verified. Thanks!").assertIsDisplayed()
    }

    @Test
    fun profile_unverifiedEmail_showsVerifyBanner() {
        val unverified = Principal("1", "dana@stacks.app", Role.MEMBER, verified = false, active = true)
        composeRule.setContent {
            LibraryTheme { ProfileContent(AuthUiState.Authenticated(unverified)) }
        }

        composeRule.onNodeWithText("Verify email").assertIsDisplayed()
    }

    @Test
    fun profile_verifiedEmail_hidesVerifyBanner() {
        val verified = Principal("1", "dana@stacks.app", Role.MEMBER, verified = true, active = true)
        composeRule.setContent {
            LibraryTheme { ProfileContent(AuthUiState.Authenticated(verified)) }
        }

        composeRule.onNodeWithText("Verify email").assertDoesNotExist()
    }
}
