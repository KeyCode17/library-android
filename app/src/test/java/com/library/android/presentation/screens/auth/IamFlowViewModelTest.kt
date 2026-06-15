package com.library.android.presentation.screens.auth

import androidx.lifecycle.SavedStateHandle
import com.library.android.MainDispatcherRule
import com.library.android.domain.model.Principal
import com.library.android.domain.repo.AuthException
import com.library.android.domain.repo.AuthRepository
import com.library.android.presentation.nav.Routes
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** ViewModel tests for the public IAM flows: forgot-password, reset-password, verify-email. */
class IamFlowViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeAuthRepository : AuthRepository {
        var forgotResult: Result<Unit> = Result.success(Unit)
        var resetResult: Result<Unit> = Result.success(Unit)
        var verifyResult: Result<Unit> = Result.success(Unit)
        var lastVerifyToken: String? = null

        override suspend fun hasSession(): Boolean = false
        override suspend fun login(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun register(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun currentUser(): Result<Principal> = Result.failure(AuthException("unused"))
        override suspend fun logout() = Unit
        override suspend fun updateEmail(email: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> =
            Result.success(Unit)
        override suspend fun deleteAccount(): Result<Unit> = Result.success(Unit)
        override suspend fun forgotPassword(email: String): Result<Unit> = forgotResult
        override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> = resetResult
        override suspend fun verifyEmail(token: String): Result<Unit> {
            lastVerifyToken = token
            return verifyResult
        }
    }

    // --- Forgot password ---

    @Test
    fun `forgot submit emits Sent on success`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = ForgotPasswordViewModel(FakeAuthRepository())

        viewModel.submit("dana@stacks.app")
        advanceUntilIdle()

        assertEquals(ForgotPasswordUiState.Sent, viewModel.state.value)
    }

    @Test
    fun `forgot submit stays neutral - failure does not leak existence`() =
        runTest(mainDispatcherRule.testDispatcher) {
            // The contract always returns 202; even a transport error is surfaced generically.
            val fake = FakeAuthRepository().apply {
                forgotResult = Result.failure(AuthException("Couldn't send the reset email (code 500)"))
            }
            val viewModel = ForgotPasswordViewModel(fake)

            viewModel.submit("ghost@stacks.app")
            advanceUntilIdle()

            assertTrue(viewModel.state.value is ForgotPasswordUiState.Error)
        }

    // --- Reset password ---

    @Test
    fun `reset success flags done`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = ResetPasswordViewModel(FakeAuthRepository(), SavedStateHandle())

        viewModel.reset("code-123", "new-password")
        advanceUntilIdle()

        assertTrue(viewModel.state.value.done)
    }

    @Test
    fun `reset reads the deep-link token from the saved state handle`() {
        val handle = SavedStateHandle(mapOf(Routes.ARG_TOKEN to "deep-link-token"))
        val viewModel = ResetPasswordViewModel(FakeAuthRepository(), handle)

        assertEquals("deep-link-token", viewModel.initialToken)
    }

    @Test
    fun `reset failure surfaces the invalid-token error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeAuthRepository().apply {
                resetResult = Result.failure(
                    AuthException("That reset link is invalid or expired, or the password is too weak"),
                )
            }
            val viewModel = ResetPasswordViewModel(fake, SavedStateHandle())

            viewModel.reset("bad", "short")
            advanceUntilIdle()

            assertEquals(
                "That reset link is invalid or expired, or the password is too weak",
                viewModel.state.value.error,
            )
        }

    // --- Verify email ---

    @Test
    fun `verify auto-runs from the deep-link token`() = runTest(mainDispatcherRule.testDispatcher) {
        val fake = FakeAuthRepository()
        val handle = SavedStateHandle(mapOf(Routes.ARG_TOKEN to "verify-token"))
        val viewModel = VerifyEmailViewModel(fake, handle)

        advanceUntilIdle()

        assertEquals(VerifyEmailUiState.Verified, viewModel.state.value)
        assertEquals("verify-token", fake.lastVerifyToken)
    }

    @Test
    fun `verify stays idle without a token until invoked manually`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = VerifyEmailViewModel(FakeAuthRepository(), SavedStateHandle())
            advanceUntilIdle()
            assertEquals(VerifyEmailUiState.Idle, viewModel.state.value)

            viewModel.verify("manual-code")
            advanceUntilIdle()

            assertEquals(VerifyEmailUiState.Verified, viewModel.state.value)
        }

    @Test
    fun `verify failure surfaces an error`() = runTest(mainDispatcherRule.testDispatcher) {
        val fake = FakeAuthRepository().apply {
            verifyResult = Result.failure(AuthException("That verification link is invalid, expired, or already used"))
        }
        val viewModel = VerifyEmailViewModel(fake, SavedStateHandle())

        viewModel.verify("bad")
        advanceUntilIdle()

        assertTrue(viewModel.state.value is VerifyEmailUiState.Error)
    }
}
