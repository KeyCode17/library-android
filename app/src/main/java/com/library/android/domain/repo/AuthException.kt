package com.library.android.domain.repo

/** Domain auth failure carrying a user-facing [message]; [cause] preserves the original error. */
class AuthException(message: String, cause: Throwable? = null) : Exception(message, cause)
