package com.library.android.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.android.domain.model.ChatMessage
import com.library.android.presentation.screens.auth.AuthTopBar
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** Stateful chat screen (the gated "Chat" tab). */
@Composable
fun ChatScreen(
    onLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ChatContent(
        state = state,
        onSelectRoom = viewModel::selectRoom,
        onSend = viewModel::send,
        onLogin = onLogin,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

/** Pure, previewable chat UI: room chips + connection + messages + composer. */
@Composable
fun ChatContent(
    state: ChatUiState,
    onSelectRoom: (String) -> Unit = {},
    onSend: (String) -> Unit = {},
    onLogin: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Chat", onBack)
        if (state.isAnonymous) {
            SignInGate(onLogin)
            return@Column
        }
        RoomChips(state.rooms, state.room, onSelectRoom)
        ConnectionBanner(state.connection)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (state.isLoading) LoadingChat() else MessagesList(state.messages)
        }
        MessageInput(onSend)
    }
}

@Composable
private fun MessagesList(messages: List<ChatMessage>) {
    if (messages.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "No messages yet — say hello.",
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
        return
    }
    // No current-user id reaches the UI (UiState is fixed), so derive sides from the thread:
    // the first/originating sender is "them" (incoming); any other sender is "me" (outgoing).
    val originator = messages.first().userId
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(messages, key = { it.id }) { message ->
            MessageRow(message, isMine = message.userId != originator)
        }
    }
}

@Composable
private fun LoadingChat() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = StacksColors.Pine,
            modifier = Modifier.testTag(ChatTestTags.LOADING),
        )
    }
}

@Composable
private fun SignInGate(onLogin: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Sign in to join the chat",
                color = StacksColors.Ink,
                fontFamily = StacksType.Display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onLogin,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StacksColors.Pine,
                    contentColor = StacksColors.OnAccent,
                ),
            ) {
                Text("Log in", fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
        }
    }
}

private val sampleMessages = listOf(
    ChatMessage("m1", "ask-a-librarian", "user-aaaa", "Is GEB available?", "2026-06-20T14:30:00Z"),
    ChatMessage("m2", "ask-a-librarian", "user-bbbb", "Yes — shelf R21.", "2026-06-20T14:31:00Z"),
)

@Preview(showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun ChatContentPreview() {
    LibraryTheme {
        ChatContent(
            ChatUiState(
                room = "ask-a-librarian",
                rooms = listOf("ask-a-librarian", "fiction", "events"),
                connection = ConnectionState.Connected,
                messages = sampleMessages,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ChatSignedOutPreview() {
    LibraryTheme { ChatContent(ChatUiState(isAnonymous = true)) }
}
