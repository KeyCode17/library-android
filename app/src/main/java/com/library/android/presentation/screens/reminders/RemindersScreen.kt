package com.library.android.presentation.screens.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.android.domain.model.Reminder
import com.library.android.domain.model.ReminderKind
import com.library.android.presentation.screens.auth.AuthTopBar
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** Stateful reminders screen (reached from Profile; gated by auth). */
@Composable
fun RemindersScreen(
    onLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: RemindersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RemindersContent(
        state = state,
        onLogin = onLogin,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

/** Pure, previewable reminders UI. */
@Composable
fun RemindersContent(
    state: RemindersUiState,
    onLogin: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Reminders", onBack)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when {
                state.isLoading -> LoadingReminders()
                state.isAnonymous -> SignInGate(onLogin)
                state.error != null -> CenteredMessage(state.error)
                state.reminders.isEmpty() -> CenteredMessage("No reminders yet.")
                else -> ReminderList(state.reminders)
            }
        }
    }
}

@Composable
private fun ReminderList(reminders: List<Reminder>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(reminders, key = { it.id }) { reminder -> ReminderRow(reminder) }
    }
}

@Composable
private fun ReminderRow(reminder: Reminder) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(StacksColors.Surface)
            .border(1.dp, StacksColors.Line, shape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        KindChip(reminder.kind)
        Text(
            text = reminder.message,
            color = StacksColors.Ink,
            fontFamily = StacksType.Body,
            fontSize = 15.sp,
        )
        Text(
            text = reminder.createdAt.substringBefore('T'),
            color = StacksColors.Faint,
            fontFamily = StacksType.Mono,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun KindChip(kind: ReminderKind) {
    val (foreground, background, label) = when (kind) {
        ReminderKind.DUE_SOON -> Triple(StacksColors.Brass700, StacksColors.Brass100, "Due soon")
        ReminderKind.OVERDUE -> Triple(StacksColors.OnAccent, StacksColors.Brass, "Overdue")
    }
    Text(
        text = label,
        color = foreground,
        fontFamily = StacksType.Body,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    )
}

@Composable
private fun LoadingReminders() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = StacksColors.Pine,
            modifier = Modifier.testTag(RemindersTestTags.LOADING),
        )
    }
}

@Composable
private fun CenteredMessage(text: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
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
                text = "Sign in to see your reminders",
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

private val sampleReminders = listOf(
    Reminder("r1", "u1", "l1", ReminderKind.DUE_SOON, "“Pale Fire” is due in 2 days.", "2026-06-20T09:00:00Z"),
    Reminder("r2", "u1", "l2", ReminderKind.OVERDUE, "“Wayfinding” is overdue.", "2026-06-18T09:00:00Z"),
)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun RemindersLoadedPreview() {
    LibraryTheme { RemindersContent(RemindersUiState(reminders = sampleReminders)) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun RemindersSignedOutPreview() {
    LibraryTheme { RemindersContent(RemindersUiState(isAnonymous = true)) }
}
