package com.library.android.domain.repo

/** Domain chat failure carrying a user-facing [message]; [cause] preserves the original. */
class ChatException(message: String, cause: Throwable? = null) : Exception(message, cause)
