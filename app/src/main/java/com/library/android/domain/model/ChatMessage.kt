package com.library.android.domain.model

/** A chat message (contract: ChatMessage). `createdAt` is an ISO-8601 string (see [Loan]). */
data class ChatMessage(
    val id: String,
    val room: String,
    val userId: String,
    val body: String,
    val createdAt: String,
)
