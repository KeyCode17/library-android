package com.library.android.presentation.screens.chat

import com.library.android.MainDispatcherRule
import com.library.android.domain.chat.ChatSocket
import com.library.android.domain.chat.ChatSocketEvent
import com.library.android.domain.model.ChatMessage
import com.library.android.domain.model.Principal
import com.library.android.domain.model.Role
import com.library.android.domain.repo.AuthException
import com.library.android.domain.repo.AuthRepository
import com.library.android.domain.repo.ChatException
import com.library.android.domain.repo.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val history = listOf(
        ChatMessage("m1", "ask-a-librarian", "u1", "hi", "2026-06-20T14:30:00Z"),
    )

    private class FakeChatRepository(var result: Result<List<ChatMessage>>) : ChatRepository {
        override suspend fun history(room: String): Result<List<ChatMessage>> = result
    }

    private class FakeChatSocket : ChatSocket {
        val events = MutableSharedFlow<ChatSocketEvent>(extraBufferCapacity = 16)
        val sent = mutableListOf<String>()
        var closed = false
        override fun open(room: String): Flow<ChatSocketEvent> = events
        override fun send(body: String) { sent += body }
        override fun close() { closed = true }
    }

    private class FakeAuthRepository(private val authenticated: Boolean) : AuthRepository {
        override suspend fun hasSession(): Boolean = authenticated
        override suspend fun login(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun register(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun currentUser(): Result<Principal> =
            if (authenticated) Result.success(Principal("u1", "u@x.io", Role.MEMBER))
            else Result.failure(AuthException("not authenticated"))
        override suspend fun logout() = Unit
    }

    private fun viewModel(
        chatResult: Result<List<ChatMessage>> = Result.success(history),
        socket: FakeChatSocket = FakeChatSocket(),
        authenticated: Boolean = true,
    ): ChatViewModel = ChatViewModel(
        chatRepository = FakeChatRepository(chatResult),
        chatSocket = socket,
        authRepository = FakeAuthRepository(authenticated),
    )

    @Test
    fun `loads history on start`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel()

        advanceUntilIdle()

        assertEquals(history, vm.state.value.messages)
        assertEquals(false, vm.state.value.isLoading)
        assertEquals(false, vm.state.value.isAnonymous)
    }

    @Test
    fun `anonymous when not authenticated`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel(authenticated = false)

        advanceUntilIdle()

        assertTrue(vm.state.value.isAnonymous)
    }

    @Test
    fun `send forwards the body to the socket`() = runTest(mainDispatcherRule.testDispatcher) {
        val socket = FakeChatSocket()
        val vm = viewModel(socket = socket)
        advanceUntilIdle()

        vm.send("hello")

        assertEquals(listOf("hello"), socket.sent)
    }

    @Test
    fun `incoming socket message is appended`() = runTest(mainDispatcherRule.testDispatcher) {
        val socket = FakeChatSocket()
        val vm = viewModel(chatResult = Result.success(emptyList()), socket = socket)
        advanceUntilIdle()

        val incoming = ChatMessage("m2", "ask-a-librarian", "u2", "yo", "2026-06-20T14:32:00Z")
        socket.events.tryEmit(ChatSocketEvent.Message(incoming))
        advanceUntilIdle()

        assertEquals(listOf(incoming), vm.state.value.messages)
    }

    @Test
    fun `connected event updates connection state`() = runTest(mainDispatcherRule.testDispatcher) {
        val socket = FakeChatSocket()
        val vm = viewModel(socket = socket)
        advanceUntilIdle()

        socket.events.tryEmit(ChatSocketEvent.Connected)
        advanceUntilIdle()

        assertEquals(ConnectionState.Connected, vm.state.value.connection)
    }

    @Test
    fun `history failure surfaces an error`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel(chatResult = Result.failure(ChatException("boom")))

        advanceUntilIdle()

        assertEquals("boom", vm.state.value.error)
    }
}
