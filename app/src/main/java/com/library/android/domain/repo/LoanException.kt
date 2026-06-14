package com.library.android.domain.repo

/** Domain lending failure carrying a user-facing [message]; [cause] preserves the original. */
class LoanException(message: String, cause: Throwable? = null) : Exception(message, cause)
