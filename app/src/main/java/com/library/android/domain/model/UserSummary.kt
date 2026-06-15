package com.library.android.domain.model

/** Admin view of a user (contract: UserSummary). `createdAt` is an ISO-8601 string. */
data class UserSummary(
    val id: String,
    val email: String,
    val role: Role,
    val verified: Boolean,
    val active: Boolean,
    val createdAt: String,
)
