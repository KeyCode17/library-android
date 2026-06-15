package com.library.android.data.remote

import com.library.android.data.remote.dto.AssignRoleRequestDto
import com.library.android.data.remote.dto.CreateUserRequestDto
import com.library.android.data.remote.dto.UpdateUserRequestDto
import com.library.android.domain.model.Role
import com.library.android.domain.model.UserSummary
import com.library.android.domain.repo.AuthException
import com.library.android.domain.repo.UserAdminRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REST-backed [UserAdminRepository]. Maps HTTP failures (incl. 403 not-admin and 409 last-admin/
 * lockout) to user-facing [AuthException] messages (typed catches only).
 */
@Singleton
class UserAdminRemoteRepository @Inject constructor(
    private val api: UserApi,
    private val ioDispatcher: CoroutineDispatcher,
) : UserAdminRepository {

    override suspend fun listUsers(): Result<List<UserSummary>> = withContext(ioDispatcher) {
        guarded({ code -> if (code == FORBIDDEN) NOT_ADMIN else "Couldn't load users (code $code)" }) {
            api.listUsers().data.map { it.toDomain() }
        }
    }

    override suspend fun createUser(
        email: String,
        password: String,
        role: Role,
    ): Result<UserSummary> = withContext(ioDispatcher) {
        guarded({ code ->
            when (code) {
                BAD_REQUEST -> "Enter a valid email and a password of at least 8 characters"
                FORBIDDEN -> NOT_ADMIN
                CONFLICT -> "That email is already registered"
                else -> "Couldn't create the user (code $code)"
            }
        }) { api.createUser(CreateUserRequestDto(email, password, role.toDto())).toDomain() }
    }

    override suspend fun updateUser(
        id: String,
        email: String?,
        active: Boolean?,
    ): Result<UserSummary> = withContext(ioDispatcher) {
        guarded({ code ->
            when (code) {
                FORBIDDEN -> NOT_ADMIN
                NOT_FOUND -> "That user no longer exists"
                CONFLICT -> "Email in use, or this would lock out the last admin"
                else -> "Couldn't update the user (code $code)"
            }
        }) { api.updateUser(id, UpdateUserRequestDto(email = email, active = active)).toDomain() }
    }

    override suspend fun deleteUser(id: String): Result<Unit> = withContext(ioDispatcher) {
        guarded({ code ->
            when (code) {
                FORBIDDEN -> NOT_ADMIN
                NOT_FOUND -> "That user no longer exists"
                CONFLICT -> "Refused — this would remove the last admin"
                else -> "Couldn't delete the user (code $code)"
            }
        }) { api.deleteUser(id) }
    }

    override suspend fun assignRole(id: String, role: Role): Result<UserSummary> =
        withContext(ioDispatcher) {
            guarded({ code ->
                when (code) {
                    FORBIDDEN -> NOT_ADMIN
                    NOT_FOUND -> "That user no longer exists"
                    CONFLICT -> "Refused — this would remove the last admin"
                    else -> "Couldn't assign the role (code $code)"
                }
            }) { api.assignRole(id, AssignRoleRequestDto(role.toDto())).toDomain() }
        }

    private inline fun <T> guarded(httpMessage: (Int) -> String, block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: HttpException) {
            Result.failure(AuthException(httpMessage(e.code()), e))
        } catch (e: IOException) {
            Result.failure(AuthException("Network error — check your connection", e))
        }

    private companion object {
        const val BAD_REQUEST = 400
        const val FORBIDDEN = 403
        const val NOT_FOUND = 404
        const val CONFLICT = 409
        const val NOT_ADMIN = "Admin access required"
    }
}
