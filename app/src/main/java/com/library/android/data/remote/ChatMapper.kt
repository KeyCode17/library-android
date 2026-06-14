package com.library.android.data.remote

import com.library.android.data.remote.dto.ChatMessageDto
import com.library.android.domain.model.ChatMessage

/** Maps the chat wire DTO to the domain model (shared by REST history and socket frames). */
fun ChatMessageDto.toDomain(): ChatMessage = ChatMessage(
    id = id,
    room = room,
    userId = userId,
    body = body,
    createdAt = createdAt,
)
