package com.library.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * IAM wire DTOs mirrored **exactly** from `library-backend/contract/openapi.yaml`
 * (components.schemas). Field names/types/nullability match the contract — anti-drift.
 */

@Serializable
enum class RoleDto {
    @SerialName("admin") ADMIN,
    @SerialName("librarian") LIBRARIAN,
    @SerialName("member") MEMBER,
}

@Serializable
data class CredentialsDto(
    val email: String,
    val password: String, // contract: minLength 8 (validated server-side)
)

@Serializable
data class AuthTokenDto(
    val token: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Long,
)

@Serializable
data class PrincipalDto(
    val id: String, // uuid
    val email: String,
    val role: RoleDto,
)

@Serializable
data class AssignRoleRequestDto(
    val role: RoleDto,
)
