package com.library.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Notification wire DTOs mirrored **exactly** from `library-backend/contract/openapi.yaml`.
 * `Notification` is the logged reminder; `DeviceRegistration` registers an FCM token.
 * Date-times stay ISO strings; snake_case via [SerialName].
 */

@Serializable
enum class PlatformDto {
    @SerialName("android") ANDROID,
    @SerialName("ios") IOS,
    @SerialName("web") WEB,
}

@Serializable
enum class ReminderKindDto {
    @SerialName("due_soon") DUE_SOON,
    @SerialName("overdue") OVERDUE,
}

@Serializable
data class DeviceRegistrationDto(
    val token: String,
    val platform: PlatformDto,
)

@Serializable
data class DeviceDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    val token: String,
    val platform: PlatformDto,
    @SerialName("registered_at") val registeredAt: String,
)

@Serializable
data class NotificationDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("loan_id") val loanId: String,
    val kind: ReminderKindDto,
    val message: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class NotificationListDto(
    val data: List<NotificationDto>,
    val pagination: PaginationDto,
)
