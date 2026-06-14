package com.library.android.domain.repo

import com.library.android.domain.model.ChatMessage

/** Port for chat history (REST). Live messages flow through [com.library.android.domain.chat.ChatSocket]. */
interface ChatRepository {
    /** Loads a room's message history (oldest first), bearer-authed. */
    suspend fun history(room: String): Result<List<ChatMessage>>
}
