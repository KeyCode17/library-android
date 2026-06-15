package com.library.android.presentation.screens.reminders

import com.library.android.MainDispatcherRule
import com.library.android.domain.model.Principal
import com.library.android.domain.model.Reminder
import com.library.android.domain.model.ReminderKind
import com.library.android.domain.model.Role
import com.library.android.domain.push.PushTokenProvider
import com.library.android.domain.repo.AuthException
import com.library.android.domain.repo.AuthRepository
import com.library.android.domain.repo.NotificationException
import com.library.android.domain.repo.NotificationRepository
import com.library.android.domain.usecase.RegisterDeviceUseCase
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RemindersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val reminders = listOf(
        Reminder("r1", "u1", "l1", ReminderKind.DUE_SOON, "due soon", "2026-06-20T09:00:00Z"),
    )

    private class FakeNotificationRepository(var result: Result<List<Reminder>>) : NotificationRepository {
        var registeredToken: String? = null
        override suspend fun registerDevice(token: String): Result<Unit> {
            registeredToken = token
            return Result.success(Unit)
        }
        override suspend fun reminders(): Result<List<Reminder>> = result
    }

    private class FakePushTokenProvider(private val token: String) : PushTokenProvider {
        override suspend fun currentToken(): Result<String> = Result.success(token)
    }

    private class FakeAuthRepository(private val authenticated: Boolean) : AuthRepository {
        override suspend fun hasSession(): Boolean = authenticated
        override suspend fun login(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun register(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun currentUser(): Result<Principal> =
            if (authenticated) Result.success(Principal("u1", "u@x.io", Role.MEMBER, verified = true, active = true))
            else Result.failure(AuthException("not authenticated"))
        override suspend fun logout() = Unit

        override suspend fun updateEmail(email: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> =
            Result.success(Unit)
        override suspend fun deleteAccount(): Result<Unit> = Result.success(Unit)
        override suspend fun forgotPassword(email: String): Result<Unit> = Result.success(Unit)
        override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> =
            Result.success(Unit)
        override suspend fun verifyEmail(token: String): Result<Unit> = Result.success(Unit)
    }

    private fun viewModel(
        repo: FakeNotificationRepository,
        authenticated: Boolean = true,
    ): RemindersViewModel = RemindersViewModel(
        notificationRepository = repo,
        registerDevice = RegisterDeviceUseCase(FakePushTokenProvider("fcm-token"), repo),
        authRepository = FakeAuthRepository(authenticated),
    )

    @Test
    fun `loads history and registers the device on open`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repo = FakeNotificationRepository(Result.success(reminders))
            val vm = viewModel(repo)

            advanceUntilIdle()

            assertEquals(reminders, vm.state.value.reminders)
            assertEquals("fcm-token", repo.registeredToken)
        }

    @Test
    fun `anonymous when not authenticated`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel(FakeNotificationRepository(Result.success(reminders)), authenticated = false)

        advanceUntilIdle()

        assertTrue(vm.state.value.isAnonymous)
    }

    @Test
    fun `history failure surfaces an error`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel(FakeNotificationRepository(Result.failure(NotificationException("boom"))))

        advanceUntilIdle()

        assertEquals("boom", vm.state.value.error)
    }
}
