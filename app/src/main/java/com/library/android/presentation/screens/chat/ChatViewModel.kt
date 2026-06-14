package com.library.android.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.chat.ChatSocket
import com.library.android.domain.chat.ChatSocketEvent
import com.library.android.domain.repo.ChatRepository
import com.library.android.domain.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives a chat room: loads REST history, opens the [ChatSocket], appends incoming messages, and
 * sends `ChatSend` frames. Auth is required (anonymous users see a sign-in gate). UDF.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val chatSocket: ChatSocket,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState(room = ROOMS.first(), rooms = ROOMS))
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    private var socketJob: Job? = null

    init {
        start(ROOMS.first())
    }

    fun selectRoom(room: String) {
        if (room != _state.value.room) start(room)
    }

    fun send(body: String) {
        if (body.isNotBlank()) chatSocket.send(body.trim())
    }

    fun retry() = start(_state.value.room)

    private fun start(room: String) {
        socketJob?.cancel()
        chatSocket.close()
        viewModelScope.launch {
            if (authRepository.currentUser().isFailure) {
                _state.value = _state.value.copy(room = room, isAnonymous = true, isLoading = false)
                return@launch
            }
            _state.value = _state.value.copy(
                room = room,
                isAnonymous = false,
                isLoading = true,
                messages = emptyList(),
                error = null,
            )
            chatRepository.history(room)
                .onSuccess { _state.value = _state.value.copy(messages = it, isLoading = false) }
                .onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
            openSocket(room)
        }
    }

    private fun openSocket(room: String) {
        _state.value = _state.value.copy(connection = ConnectionState.Connecting)
        socketJob = chatSocket.open(room)
            .onEach(::onSocketEvent)
            .launchIn(viewModelScope)
    }

    private fun onSocketEvent(event: ChatSocketEvent) {
        _state.value = when (event) {
            ChatSocketEvent.Connected ->
                _state.value.copy(connection = ConnectionState.Connected)
            is ChatSocketEvent.Message ->
                _state.value.copy(messages = _state.value.messages + event.message)
            is ChatSocketEvent.Failed ->
                _state.value.copy(connection = ConnectionState.Disconnected, error = event.reason)
            ChatSocketEvent.Closed ->
                _state.value.copy(connection = ConnectionState.Disconnected)
        }
    }

    override fun onCleared() {
        chatSocket.close()
        super.onCleared()
    }

    private companion object {
        // Room kinds from the contract: an event id, a book category, and ask-a-librarian.
        val ROOMS = listOf("ask-a-librarian", "fiction", "events")
    }
}
