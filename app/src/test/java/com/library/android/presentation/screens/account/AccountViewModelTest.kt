package com.library.android.presentation.screens.account

import com.library.android.MainDispatcherRule
import com.library.android.domain.model.Principal
import com.library.android.domain.model.Role
import com.library.android.domain.repo.AuthException
import com.library.android.domain.repo.AuthRepository
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AccountViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val principal = Principal("1", "dana@stacks.app", Role.MEMBER, verified = true, active = true)

    /** Fake auth port — self-service results are configurable per test. */
    private class FakeAuthRepository : AuthRepository {
        var meResult: Result<Principal> = Result.failure(AuthException("unset"))
        var updateEmailResult: Result<Principal> = Result.failure(AuthException("unset"))
        var changePasswordResult: Result<Unit> = Result.success(Unit)
        var deleteAccountResult: Result<Unit> = Result.success(Unit)

        override suspend fun hasSession(): Boolean = true
        override suspend fun login(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun register(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun currentUser(): Result<Principal> = meResult
        override suspend fun logout() = Unit
        override suspend fun updateEmail(email: String): Result<Principal> = updateEmailResult
        override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> =
            changePasswordResult
        override suspend fun deleteAccount(): Result<Unit> = deleteAccountResult
        override suspend fun forgotPassword(email: String): Result<Unit> = Result.success(Unit)
        override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> =
            Result.success(Unit)
        override suspend fun verifyEmail(token: String): Result<Unit> = Result.success(Unit)
    }

    @Test
    fun `load populates the current email`() = runTest(mainDispatcherRule.testDispatcher) {
        val fake = FakeAuthRepository().apply { meResult = Result.success(principal) }
        val viewModel = AccountViewModel(fake)

        advanceUntilIdle()

        assertEquals("dana@stacks.app", viewModel.state.value.email)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `updateEmail success reflects the new email and a confirmation`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeAuthRepository().apply {
                meResult = Result.success(principal)
                updateEmailResult = Result.success(principal.copy(email = "new@stacks.app"))
            }
            val viewModel = AccountViewModel(fake)
            advanceUntilIdle()

            viewModel.updateEmail("new@stacks.app")
            advanceUntilIdle()

            assertEquals("new@stacks.app", viewModel.state.value.email)
            assertEquals("Email updated", viewModel.state.value.message)
        }

    @Test
    fun `changePassword success surfaces a confirmation`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeAuthRepository().apply { meResult = Result.success(principal) }
            val viewModel = AccountViewModel(fake)
            advanceUntilIdle()

            viewModel.changePassword("old-password", "new-password")
            advanceUntilIdle()

            assertEquals("Password changed", viewModel.state.value.message)
        }

    @Test
    fun `changePassword with a wrong current password surfaces the error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeAuthRepository().apply {
                meResult = Result.success(principal)
                changePasswordResult =
                    Result.failure(AuthException("Your current password is incorrect"))
            }
            val viewModel = AccountViewModel(fake)
            advanceUntilIdle()

            viewModel.changePassword("wrong", "new-password")
            advanceUntilIdle()

            assertEquals("Your current password is incorrect", viewModel.state.value.message)
        }

    @Test
    fun `deleteAccount success flags deletion for navigation`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeAuthRepository().apply { meResult = Result.success(principal) }
            val viewModel = AccountViewModel(fake)
            advanceUntilIdle()

            viewModel.deleteAccount()
            advanceUntilIdle()

            assertTrue(viewModel.state.value.deleted)
        }

    @Test
    fun `deleteAccount failure surfaces the last-admin error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeAuthRepository().apply {
                meResult = Result.success(principal)
                deleteAccountResult =
                    Result.failure(AuthException("You're the last admin — assign another admin first"))
            }
            val viewModel = AccountViewModel(fake)
            advanceUntilIdle()

            viewModel.deleteAccount()
            advanceUntilIdle()

            assertFalse(viewModel.state.value.deleted)
            assertEquals(
                "You're the last admin — assign another admin first",
                viewModel.state.value.message,
            )
        }
}
