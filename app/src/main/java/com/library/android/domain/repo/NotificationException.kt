package com.library.android.domain.repo

/** Domain notification failure carrying a user-facing [message]; [cause] preserves the original. */
class NotificationException(message: String, cause: Throwable? = null) : Exception(message, cause)
