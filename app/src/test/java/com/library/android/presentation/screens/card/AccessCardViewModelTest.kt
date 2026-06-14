package com.library.android.presentation.screens.card

import com.library.android.MainDispatcherRule
import com.library.android.domain.device.QrEncoder
import com.library.android.domain.device.QrMatrix
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

class AccessCardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val matrix = QrMatrix(1, booleanArrayOf(true))

    private class FakeAuthRepository(private val authenticated: Boolean) : AuthRepository {
        override suspend fun hasSession(): Boolean = authenticated
        override suspend fun login(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun register(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun currentUser(): Result<Principal> =
            if (authenticated) Result.success(Principal("u1", "dana@stacks.app", Role.MEMBER))
            else Result.failure(AuthException("not authenticated"))
        override suspend fun logout() = Unit
    }

    private class FakeQrEncoder(private val result: Result<QrMatrix>) : QrEncoder {
        var lastContent: String? = null
        override fun encode(content: String): Result<QrMatrix> {
            lastContent = content
            return result
        }
    }

    @Test
    fun `encodes the membership reference when authenticated`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val encoder = FakeQrEncoder(Result.success(matrix))
            val vm = AccessCardViewModel(FakeAuthRepository(true), encoder)

            advanceUntilIdle()

            val state = vm.state.value
            assertTrue("expected Ready but was $state", state is AccessCardUiState.Ready)
            state as AccessCardUiState.Ready
            assertEquals("u1", state.membershipId)
            assertEquals("dana@stacks.app", state.email)
            assertEquals("library-member:u1", encoder.lastContent)
        }

    @Test
    fun `anonymous when not authenticated`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = AccessCardViewModel(FakeAuthRepository(false), FakeQrEncoder(Result.success(matrix)))

        advanceUntilIdle()

        assertEquals(AccessCardUiState.Anonymous, vm.state.value)
    }

    @Test
    fun `error when encoding fails`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = AccessCardViewModel(
            FakeAuthRepository(true),
            FakeQrEncoder(Result.failure(IllegalStateException("nope"))),
        )

        advanceUntilIdle()

        assertTrue(vm.state.value is AccessCardUiState.Error)
    }
}
