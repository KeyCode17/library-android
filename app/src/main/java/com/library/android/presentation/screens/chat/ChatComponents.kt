package com.library.android.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.library.android.domain.model.ChatMessage
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** Horizontally scrollable room selector. */
@Composable
fun RoomChips(rooms: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        rooms.forEach { room -> RoomChip(room, room == selected) { onSelect(room) } }
    }
}

@Composable
private fun RoomChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(999.dp)
    Text(
        text = label,
        color = if (selected) StacksColors.OnAccent else StacksColors.Muted,
        fontFamily = StacksType.Body,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        modifier = Modifier
            .minimumInteractiveComponentSize() // ≥48dp touch target for a11y
            .clip(shape)
            .background(if (selected) StacksColors.Pine else StacksColors.Surface)
            .border(1.dp, if (selected) StacksColors.Pine else StacksColors.Line, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

/**
 * A single chat message bubble. Incoming bubbles (left, surface) carry a sender name; outgoing
 * "me" bubbles (right, pine) drop it. `isMine` is hoisted from the list, so the public signature
 * stays callable as `MessageRow(message)`.
 */
@Composable
fun MessageRow(message: ChatMessage, isMine: Boolean = false) {
    Box(modifier = Modifier.fillMaxWidth()) {
        MessageBubble(
            message = message,
            isMine = isMine,
            modifier = Modifier
                .align(if (isMine) Alignment.CenterEnd else Alignment.CenterStart)
                .widthIn(max = BUBBLE_MAX_WIDTH),
        )
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, isMine: Boolean, modifier: Modifier = Modifier) {
    val shape = bubbleShape(isMine)
    val background = if (isMine) StacksColors.Pine else StacksColors.Surface
    val bodyColor = if (isMine) StacksColors.OnAccent else StacksColors.Ink
    Column(
        modifier = modifier
            .clip(shape)
            .background(background)
            .then(if (isMine) Modifier else Modifier.border(1.dp, StacksColors.Line, shape))
            .padding(horizontal = 13.dp, vertical = 9.dp),
    ) {
        if (!isMine) {
            Text(
                text = message.userId.take(USER_PREFIX),
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
        Text(
            text = message.body,
            color = bodyColor,
            fontFamily = StacksType.Body,
            fontSize = 14.sp,
        )
    }
}

/** Asymmetric bubble: a small "tail" corner on the sender's side (left for them, right for me). */
private fun bubbleShape(isMine: Boolean): RoundedCornerShape = if (isMine) {
    RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 14.dp, bottomEnd = 4.dp)
} else {
    RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 4.dp, bottomEnd = 14.dp)
}

/** Message composer: pill input + round pine send button. The draft is ephemeral UI state. */
@Composable
fun MessageInput(onSend: (String) -> Unit) {
    var draft by remember { mutableStateOf("") }
    val submit = {
        if (draft.isNotBlank()) {
            onSend(draft.trim())
            draft = ""
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(StacksColors.Surface)
            .border(width = 1.dp, color = StacksColors.Line)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = draft,
            onValueChange = { draft = it },
            placeholder = {
                Text("Message…", fontFamily = StacksType.Body, color = StacksColors.Faint)
            },
            singleLine = true,
            shape = RoundedCornerShape(999.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { submit() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = StacksColors.Bg,
                unfocusedContainerColor = StacksColors.Bg,
                focusedBorderColor = StacksColors.Line,
                unfocusedBorderColor = StacksColors.Line,
                cursorColor = StacksColors.Pine,
            ),
            modifier = Modifier.weight(1f).testTag(ChatTestTags.INPUT),
        )
        SendButton(onClick = submit)
    }
}

/** Round pine send button. Keeps the "Send" label (a11y + test contract) inside the circle. */
@Composable
private fun SendButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize() // ≥48dp touch target for a11y
            .size(40.dp)
            .clip(CircleShape)
            .background(StacksColors.Pine)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Send",
            color = StacksColors.OnAccent,
            fontFamily = StacksType.Body,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
        )
    }
}

/** Connection status indicator: a status dot + label, mirroring the design's header badge. */
@Composable
fun ConnectionBanner(connection: ConnectionState) {
    val (color, label) = when (connection) {
        ConnectionState.Connected -> StacksColors.Pine to "Connected"
        ConnectionState.Connecting -> StacksColors.Brass700 to "Connecting…"
        ConnectionState.Disconnected -> StacksColors.Faint to "Disconnected"
    }
    Row(
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = label,
            color = color,
            fontFamily = StacksType.Body,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
        )
    }
}

private const val USER_PREFIX = 8
private val BUBBLE_MAX_WIDTH = 280.dp
