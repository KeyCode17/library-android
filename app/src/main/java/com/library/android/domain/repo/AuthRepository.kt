package com.library.android.domain.repo

import com.library.android.domain.model.Principal

/**
 * Port for authentication and account self-service. One cohesive boundary over the IAM `/auth`
 * surface (session + self-service + the public email flows).
 */
@Suppress("TooManyFunctions") // cohesive IAM auth surface, not a god interface
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

    // --- IAM v2 self-service ---

    /** Updates the current user's email (`PATCH /auth/me`). */
    suspend fun updateEmail(email: String): Result<Principal>

    /** Changes the current user's password (`POST /auth/change-password`). */
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>

    /** Deletes the current user's account (`DELETE /auth/me`) and clears the token. */
    suspend fun deleteAccount(): Result<Unit>

    // --- IAM v2 email flows (public; token from the emailed link) ---

    /** Requests a password-reset email (`POST /auth/forgot-password`); always neutral. */
    suspend fun forgotPassword(email: String): Result<Unit>

    /** Resets the password with a token (`POST /auth/reset-password`). */
    suspend fun resetPassword(token: String, newPassword: String): Result<Unit>

    /** Verifies an email with a token (`POST /auth/verify-email`). */
    suspend fun verifyEmail(token: String): Result<Unit>
}
