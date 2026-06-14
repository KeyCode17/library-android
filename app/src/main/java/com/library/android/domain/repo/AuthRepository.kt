package com.library.android.domain.repo

import com.library.android.domain.model.Principal

/**
 * Port for authentication. The data layer logs in/out against the IAM endpoints and owns the
 * bearer token's secure storage; the domain only sees [Principal]s and `Result`s.
 */
interface AuthRepository {
    /** Whether a token is currently stored (cheap startup check). */
    suspend fun hasSession(): Boolean

    /** Exchanges credentials for a token, stores it, and returns the resulting principal. */
    suspend fun login(email: String, password: String): Result<Principal>

    /** Self-registers a member (does not log in — the caller logs in afterwards). */
    suspend fun register(email: String, password: String): Result<Principal>

    /** Reads `/auth/me` using the stored token. */
    suspend fun currentUser(): Result<Principal>

    /** Clears the stored token. */
    suspend fun logout()
}
