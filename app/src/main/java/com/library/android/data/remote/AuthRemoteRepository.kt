package com.library.android.data.remote

import com.library.android.data.auth.TokenStorage
import com.library.android.data.remote.dto.ChangePasswordRequestDto
import com.library.android.data.remote.dto.CredentialsDto
import com.library.android.data.remote.dto.ForgotPasswordRequestDto
import com.library.android.data.remote.dto.ResetPasswordRequestDto
import com.library.android.data.remote.dto.UpdateMeRequestDto
import com.library.android.data.remote.dto.VerifyEmailRequestDto
import com.library.android.domain.model.Principal
import com.library.android.domain.repo.AuthException
import com.library.android.domain.repo.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REST-backed [AuthRepository]. Owns the token lifecycle: login stores it, logout clears it.
 * HTTP failures are mapped to user-facing [AuthException] messages (typed catches only).
 */
@Singleton
@Suppress("TooManyFunctions") // cohesive IAM auth surface (one /auth REST mapper)
class AuthRemoteRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
    private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override suspend fun hasSession(): Boolean = withContext(ioDispatcher) {
        tokenStorage.token() != null
    }

    override suspend fun login(email: String, password: String): Result<Principal> =
        withContext(ioDispatcher) {
            try {
                val token = api.login(CredentialsDto(email, password))
                tokenStorage.save(token.token)
                Result.success(api.me().toDomain())
            } catch (e: HttpException) {
                val message = if (e.code() == HTTP_UNAUTHORIZED) {
                    "Invalid email or password"
                } else {
                    "Login failed (${e.code()})"
                }
                Result.failure(AuthException(message, e))
            } catch (e: IOException) {
                Result.failure(AuthException("Network error — check your connection", e))
            }
        }

    override suspend fun register(email: String, password: String): Result<Principal> =
        withContext(ioDispatcher) {
            try {
                Result.success(api.register(CredentialsDto(email, password)).toDomain())
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    HTTP_BAD_REQUEST -> "Enter a valid email and a password of at least 8 characters"
                    HTTP_CONFLICT -> "That email is already registered"
                    else -> "Registration failed (${e.code()})"
                }
                Result.failure(AuthException(message, e))
            } catch (e: IOException) {
                Result.failure(AuthException("Network error — check your connection", e))
            }
        }

    override suspend fun currentUser(): Result<Principal> = withContext(ioDispatcher) {
        try {
            Result.success(api.me().toDomain())
        } catch (e: HttpException) {
            Result.failure(AuthException("Session expired — please sign in again", e))
        } catch (e: IOException) {
            Result.failure(AuthException("Network error — check your connection", e))
        }
    }

    override suspend fun logout() {
        withContext(ioDispatcher) { tokenStorage.clear() }
    }

    override suspend fun updateEmail(email: String): Result<Principal> = withContext(ioDispatcher) {
        guarded({ code ->
            when (code) {
                HTTP_UNAUTHORIZED -> "Sign in to update your email"
                HTTP_CONFLICT -> "That email is already in use"
                else -> "Couldn't update your email (code $code)"
            }
        }) { api.updateMe(UpdateMeRequestDto(email)).toDomain() }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
    ): Result<Unit> = withContext(ioDispatcher) {
        guarded({ code ->
            when (code) {
                HTTP_UNAUTHORIZED -> "Your current password is incorrect"
                HTTP_BAD_REQUEST -> "New password must be at least 8 characters"
                else -> "Couldn't change your password (code $code)"
            }
        }) { api.changePassword(ChangePasswordRequestDto(currentPassword, newPassword)) }
    }

    override suspend fun deleteAccount(): Result<Unit> = withContext(ioDispatcher) {
        guarded({ code ->
            when (code) {
                HTTP_UNAUTHORIZED -> "Sign in to delete your account"
                HTTP_CONFLICT -> "You're the last admin — assign another admin first"
                else -> "Couldn't delete your account (code $code)"
            }
        }) { api.deleteMe() }.onSuccess { tokenStorage.clear() }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> = withContext(ioDispatcher) {
        guarded({ code -> "Couldn't send the reset email (code $code)" }) {
            api.forgotPassword(ForgotPasswordRequestDto(email))
        }
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> =
        withContext(ioDispatcher) {
            guarded({ code ->
                if (code == HTTP_BAD_REQUEST) {
                    "That reset link is invalid or expired, or the password is too weak"
                } else {
                    "Couldn't reset your password (code $code)"
                }
            }) { api.resetPassword(ResetPasswordRequestDto(token, newPassword)) }
        }

    override suspend fun verifyEmail(token: String): Result<Unit> = withContext(ioDispatcher) {
        guarded({ code ->
            if (code == HTTP_BAD_REQUEST) {
                "That verification link is invalid, expired, or already used"
            } else {
                "Couldn't verify your email (code $code)"
            }
        }) { api.verifyEmail(VerifyEmailRequestDto(token)) }
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
        const val HTTP_BAD_REQUEST = 400
        const val HTTP_UNAUTHORIZED = 401
        const val HTTP_CONFLICT = 409
    }
}
