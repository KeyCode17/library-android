package com.library.android.domain.model

/** The authenticated user (contract: Principal — a user record without the password hash). */
data class Principal(
    val id: String,
    val email: String,
    val role: Role,
)
