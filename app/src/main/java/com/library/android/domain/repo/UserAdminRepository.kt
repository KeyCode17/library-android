package com.library.android.domain.repo

import com.library.android.domain.model.Role
import com.library.android.domain.model.UserSummary

/**
 * Port for admin user management (`/users`). All calls require an admin bearer token; the server
 * enforces the role and the last-admin/lockout safeguards regardless of the client gate.
 */
interface UserAdminRepository {
    suspend fun listUsers(): Result<List<UserSummary>>
    suspend fun createUser(email: String, password: String, role: Role): Result<UserSummary>
    suspend fun updateUser(id: String, email: String?, active: Boolean?): Result<UserSummary>
    suspend fun deleteUser(id: String): Result<Unit>
    suspend fun assignRole(id: String, role: Role): Result<UserSummary>
}
