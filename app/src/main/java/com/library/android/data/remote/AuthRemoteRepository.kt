package com.library.android.data.remote

import com.library.android.data.auth.TokenStorage
import com.library.android.data.remote.dto.CredentialsDto
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

    private companion object {
        const val HTTP_BAD_REQUEST = 400
        const val HTTP_UNAUTHORIZED = 401
        const val HTTP_CONFLICT = 409
    }
}
