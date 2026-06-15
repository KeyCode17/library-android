package com.library.android.presentation.screens.admin

import com.library.android.MainDispatcherRule
import com.library.android.domain.model.Principal
import com.library.android.domain.model.Role
import com.library.android.domain.model.UserSummary
import com.library.android.domain.repo.AuthException
import com.library.android.domain.repo.AuthRepository
import com.library.android.domain.repo.UserAdminRepository
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ManageUsersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val admin = Principal("1", "ada@stacks.app", Role.ADMIN, verified = true, active = true)
    private val member = Principal("2", "mem@stacks.app", Role.MEMBER, verified = true, active = true)
    private val users = listOf(
        UserSummary("1", "ada@stacks.app", Role.ADMIN, verified = true, active = true, createdAt = "t"),
        UserSummary("2", "mem@stacks.app", Role.MEMBER, verified = false, active = true, createdAt = "t"),
    )

    private class FakeAuthRepository(private val me: Result<Principal>) : AuthRepository {
        override suspend fun hasSession(): Boolean = true
        override suspend fun login(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun register(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun currentUser(): Result<Principal> = me
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

    private class FakeUserAdminRepository(
        var listResult: Result<List<UserSummary>>,
    ) : UserAdminRepository {
        var deleteResult: Result<Unit> = Result.success(Unit)
        var lastDeletedId: String? = null
        var lastAssignedRole: Pair<String, Role>? = null

        override suspend fun listUsers(): Result<List<UserSummary>> = listResult
        override suspend fun createUser(email: String, password: String, role: Role): Result<UserSummary> =
            Result.success(UserSummary("9", email, role, verified = false, active = true, createdAt = "t"))
        override suspend fun updateUser(id: String, email: String?, active: Boolean?): Result<UserSummary> =
            Result.success(UserSummary(id, "x@stacks.app", Role.MEMBER, false, active ?: true, "t"))
        override suspend fun deleteUser(id: String): Result<Unit> {
            lastDeletedId = id
            return deleteResult
        }
        override suspend fun assignRole(id: String, role: Role): Result<UserSummary> {
            lastAssignedRole = id to role
            return Result.success(UserSummary(id, "x@stacks.app", role, false, true, "t"))
        }
    }

    @Test
    fun `non-admin is gated as forbidden`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = ManageUsersViewModel(
            FakeUserAdminRepository(Result.success(users)),
            FakeAuthRepository(Result.success(member)),
        )

        advanceUntilIdle()

        assertTrue(viewModel.state.value.isForbidden)
        assertTrue(viewModel.state.value.users.isEmpty())
    }

    @Test
    fun `admin loads the user list`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = ManageUsersViewModel(
            FakeUserAdminRepository(Result.success(users)),
            FakeAuthRepository(Result.success(admin)),
        )

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isForbidden)
        assertEquals(2, viewModel.state.value.users.size)
    }

    @Test
    fun `assignRole refreshes with a confirmation`() = runTest(mainDispatcherRule.testDispatcher) {
        val adminRepo = FakeUserAdminRepository(Result.success(users))
        val viewModel = ManageUsersViewModel(adminRepo, FakeAuthRepository(Result.success(admin)))
        advanceUntilIdle()

        viewModel.assignRole("2", Role.LIBRARIAN)
        advanceUntilIdle()

        assertEquals("2" to Role.LIBRARIAN, adminRepo.lastAssignedRole)
        assertEquals("Role updated", viewModel.state.value.message)
    }

    @Test
    fun `deleteUser surfaces a last-admin refusal`() = runTest(mainDispatcherRule.testDispatcher) {
        val adminRepo = FakeUserAdminRepository(Result.success(users)).apply {
            deleteResult = Result.failure(AuthException("Refused — this would remove the last admin"))
        }
        val viewModel = ManageUsersViewModel(adminRepo, FakeAuthRepository(Result.success(admin)))
        advanceUntilIdle()

        viewModel.deleteUser("1")
        advanceUntilIdle()

        assertEquals("1", adminRepo.lastDeletedId)
        assertEquals("Refused — this would remove the last admin", viewModel.state.value.message)
    }

    @Test
    fun `forbidden when the session is missing`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = ManageUsersViewModel(
            FakeUserAdminRepository(Result.success(users)),
            FakeAuthRepository(Result.failure(AuthException("Session expired"))),
        )

        advanceUntilIdle()

        assertTrue(viewModel.state.value.isForbidden)
    }
}
