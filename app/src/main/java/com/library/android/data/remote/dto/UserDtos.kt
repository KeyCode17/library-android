package com.library.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Admin user-management wire DTOs mirrored **exactly** from
 * `library-backend/contract/openapi.yaml` (components.schemas). snake_case via [SerialName].
 */

@Serializable
data class UserSummaryDto(
    val id: String,
    val email: String,
    val role: RoleDto,
    val verified: Boolean,
    val active: Boolean,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class UserListDto(
    val data: List<UserSummaryDto>,
    val pagination: PaginationDto,
)

@Serializable
data class CreateUserRequestDto(
    val email: String,
    val password: String,
    val role: RoleDto,
)

/** Any subset of the updatable fields (both optional, per the contract). */
@Serializable
data class UpdateUserRequestDto(
    val email: String? = null,
    val active: Boolean? = null,
)
