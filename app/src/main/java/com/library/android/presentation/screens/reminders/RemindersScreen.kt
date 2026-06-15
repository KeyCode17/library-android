@file:Suppress("TooManyFunctions") // Compose screen: many small, previewable composables

package com.library.android.presentation.screens.reminders

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
                state.reminders.isEmpty() -> EmptyReminders()
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
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KindChip(reminder.kind)
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = reminder.message,
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontSize = 13.sp,
        )
        Spacer(Modifier.height(8.dp))
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
        fontSize = 11.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    )
}

@Composable
private fun EmptyReminders() {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BellGlyph()
            Text(
                text = "No reminders yet",
                color = StacksColors.Ink,
                fontFamily = StacksType.Display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "When a borrowed book is due soon or overdue, a reminder will " +
                    "show up here so you never miss a return.",
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Outlined bell, mirroring the empty-state SVG in `docs/designs/reminders.html`. */
@Composable
private fun BellGlyph() {
    Canvas(Modifier.size(44.dp)) {
        val u = size.width / 24f
        val stroke = Stroke(width = 1.6f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val bell = Path().apply {
            moveTo(18f * u, 8f * u)
            cubicTo(18f * u, 4.7f * u, 15.3f * u, 2f * u, 12f * u, 2f * u)
            cubicTo(8.7f * u, 2f * u, 6f * u, 4.7f * u, 6f * u, 8f * u)
            cubicTo(6f * u, 15f * u, 3f * u, 17f * u, 3f * u, 17f * u)
            lineTo(21f * u, 17f * u)
            cubicTo(21f * u, 17f * u, 18f * u, 15f * u, 18f * u, 8f * u)
        }
        val clapper = Path().apply {
            moveTo(13.7f * u, 21f * u)
            cubicTo(13.0f * u, 22.2f * u, 11.0f * u, 22.2f * u, 10.3f * u, 21f * u)
        }
        drawPath(bell, color = StacksColors.Faint, style = stroke)
        drawPath(clapper, color = StacksColors.Faint, style = stroke)
    }
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
private fun RemindersEmptyPreview() {
    LibraryTheme { RemindersContent(RemindersUiState(reminders = emptyList())) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun RemindersSignedOutPreview() {
    LibraryTheme { RemindersContent(RemindersUiState(isAnonymous = true)) }
}
