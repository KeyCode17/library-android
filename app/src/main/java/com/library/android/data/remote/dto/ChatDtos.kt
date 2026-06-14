package com.library.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Chat wire DTOs mirrored **exactly** from `library-backend/contract/openapi.yaml`.
 * `ChatMessage` is the server→client frame (and REST history item); `ChatSend` is the
 * client→server socket frame. `created_at` stays an ISO-8601 string. snake_case via [SerialName].
 */

@Serializable
data class ChatMessageDto(
    val id: String,
    val room: String,
    @SerialName("user_id") val userId: String,
    val body: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class ChatSendDto(
    val body: String,
)

@Serializable
data class ChatMessageListDto(
    val data: List<ChatMessageDto>,
    val pagination: PaginationDto,
)
