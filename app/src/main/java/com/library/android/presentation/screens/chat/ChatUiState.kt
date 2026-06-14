package com.library.android.presentation.screens.chat

import androidx.compose.runtime.Immutable
import com.library.android.domain.model.ChatMessage

/** WebSocket connection state surfaced to the UI. */
enum class ConnectionState { Connecting, Connected, Disconnected }

/** Chat UiState: the selected room, loaded history + live messages, and connection state. */
@Immutable
data class ChatUiState(
    val room: String = "",
    val rooms: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isAnonymous: Boolean = false,
    val connection: ConnectionState = ConnectionState.Disconnected,
    val messages: List<ChatMessage> = emptyList(),
    val error: String? = null,
)
