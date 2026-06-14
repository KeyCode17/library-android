package com.library.android.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
            .clip(shape)
            .background(if (selected) StacksColors.Pine else StacksColors.Surface)
            .border(1.dp, if (selected) StacksColors.Pine else StacksColors.Line, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

/** A single chat message: body + author/time meta. */
@Composable
fun MessageRow(message: ChatMessage) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(StacksColors.Surface)
            .border(1.dp, StacksColors.Line, shape)
            .padding(12.dp),
    ) {
        Text(
            text = message.body,
            color = StacksColors.Ink,
            fontFamily = StacksType.Body,
            fontSize = 15.sp,
        )
        Text(
            text = "${message.userId.take(USER_PREFIX)} · ${message.createdAt.timeOfDay()}",
            color = StacksColors.Faint,
            fontFamily = StacksType.Mono,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

/** Message composer; the draft is ephemeral UI state. */
@Composable
fun MessageInput(onSend: (String) -> Unit) {
    var draft by remember { mutableStateOf("") }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = draft,
            onValueChange = { draft = it },
            placeholder = { Text("Message") },
            singleLine = true,
            modifier = Modifier.weight(1f).testTag(ChatTestTags.INPUT),
        )
        Button(
            onClick = {
                if (draft.isNotBlank()) {
                    onSend(draft)
                    draft = ""
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StacksColors.Pine,
                contentColor = StacksColors.OnAccent,
            ),
        ) {
            Text("Send", fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}

/** Inline connection status line. */
@Composable
fun ConnectionBanner(connection: ConnectionState) {
    val (color, label) = when (connection) {
        ConnectionState.Connected -> StacksColors.Pine to "Connected"
        ConnectionState.Connecting -> StacksColors.Brass700 to "Connecting…"
        ConnectionState.Disconnected -> StacksColors.Faint to "Disconnected"
    }
    Text(
        text = label,
        color = color,
        fontFamily = StacksType.Body,
        fontSize = 11.sp,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 2.dp),
    )
}

private const val USER_PREFIX = 8

private fun String.timeOfDay(): String = substringAfter('T', "").take(5)
