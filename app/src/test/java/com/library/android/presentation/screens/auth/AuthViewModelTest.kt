package com.library.android.presentation.screens.auth

import com.library.android.MainDispatcherRule
import com.library.android.domain.model.Principal
import com.library.android.domain.model.Role
import com.library.android.domain.repo.AuthException
import com.library.android.domain.repo.AuthRepository
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val principal = Principal("1", "dana@stacks.app", Role.MEMBER)

    /** Fake auth port — token state is just a boolean; results are configurable per test. */
    private class FakeAuthRepository : AuthRepository {
        var session = false
        var loginResult: Result<Principal> = Result.failure(AuthException("unset"))
        var registerResult: Result<Principal> = Result.failure(AuthException("unset"))
        var meResult: Result<Principal> = Result.failure(AuthException("unset"))
        var logoutCalled = false

        override suspend fun hasSession(): Boolean = session

        override suspend fun login(email: String, password: String): Result<Principal> {
            if (loginResult.isSuccess) session = true
            return loginResult
        }

        override suspend fun register(email: String, password: String): Result<Principal> =
            registerResult

        override suspend fun currentUser(): Result<Principal> = meResult

        override suspend fun logout() {
            session = false
            logoutCalled = true
        }
    }

    @Test
    fun `login success emits authenticated`() = runTest(mainDispatcherRule.testDispatcher) {
        val fake = FakeAuthRepository().apply { loginResult = Result.success(principal) }
        val viewModel = AuthViewModel(fake)
        advanceUntilIdle() // initial refreshSession -> Anonymous

        viewModel.login("dana@stacks.app", "password1")
        advanceUntilIdle()

        assertEquals(AuthUiState.Authenticated(principal), viewModel.state.value)
    }

    @Test
    fun `login failure emits error`() = runTest(mainDispatcherRule.testDispatcher) {
        val fake = FakeAuthRepository().apply {
            loginResult = Result.failure(AuthException("Invalid email or password"))
        }
        val viewModel = AuthViewModel(fake)
        advanceUntilIdle()

        viewModel.login("dana@stacks.app", "wrong")
        advanceUntilIdle()

        assertEquals(AuthUiState.Error("Invalid email or password"), viewModel.state.value)
    }

    @Test
    fun `refreshSession with a stored token loads the current user`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeAuthRepository().apply {
                session = true
                meResult = Result.success(principal)
            }
            val viewModel = AuthViewModel(fake)

            advanceUntilIdle()

            assertEquals(AuthUiState.Authenticated(principal), viewModel.state.value)
        }

    @Test
    fun `refreshSession with no token stays anonymous`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = AuthViewModel(FakeAuthRepository())

            advanceUntilIdle()

            assertEquals(AuthUiState.Anonymous, viewModel.state.value)
        }

    @Test
    fun `logout clears the session and returns to anonymous`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeAuthRepository().apply {
                session = true
                meResult = Result.success(principal)
            }
            val viewModel = AuthViewModel(fake)
            advanceUntilIdle()

            viewModel.logout()
            advanceUntilIdle()

            assertEquals(AuthUiState.Anonymous, viewModel.state.value)
            assertTrue(fake.logoutCalled)
        }

    @Test
    fun `register success auto-logs-in to authenticated`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeAuthRepository().apply {
                registerResult = Result.success(principal)
                loginResult = Result.success(principal)
            }
            val viewModel = AuthViewModel(fake)
            advanceUntilIdle()

            viewModel.register("dana@stacks.app", "password1")
            advanceUntilIdle()

            assertEquals(AuthUiState.Authenticated(principal), viewModel.state.value)
        }
}
