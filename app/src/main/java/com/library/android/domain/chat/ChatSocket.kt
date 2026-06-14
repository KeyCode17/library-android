package com.library.android.domain.chat

import com.library.android.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * Port for the live chat WebSocket. The production adapter connects to
 * `/ws/chat?room=<room>&token=<jwt>`; tests use a fake. Keeping it an interface means the chat
 * flow is unit-testable without a real socket.
 */
interface ChatSocket {
    /** Opens a connection to [room] and emits connection + incoming-message events until cancelled. */
    fun open(room: String): Flow<ChatSocketEvent>

    /** Sends a message body on the current connection (a `ChatSend` frame). */
    fun send(body: String)

    /** Closes the current connection. */
    fun close()
}

/** Events surfaced by a [ChatSocket] connection. */
sealed interface ChatSocketEvent {
    data object Connected : ChatSocketEvent
    data class Message(val message: ChatMessage) : ChatSocketEvent
    data class Failed(val reason: String) : ChatSocketEvent
    data object Closed : ChatSocketEvent
}
