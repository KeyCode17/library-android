package com.library.android.presentation.screens.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.android.presentation.screens.auth.AuthErrorText
import com.library.android.presentation.screens.auth.AuthPrimaryButton
import com.library.android.presentation.screens.auth.AuthTextField
import com.library.android.presentation.screens.auth.AuthTopBar
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** Stateful account self-service screen (reached from Profile; gated by auth upstream). */
@Composable
fun AccountScreen(
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.deleted) { if (state.deleted) onDeleted() }
    AccountContent(
        state = state,
        onUpdateEmail = viewModel::updateEmail,
        onChangePassword = viewModel::changePassword,
        onDeleteAccount = viewModel::deleteAccount,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

@Composable
fun AccountContent(
    state: AccountUiState,
    onUpdateEmail: (String) -> Unit = {},
    onChangePassword: (String, String) -> Unit = { _, _ -> },
    onDeleteAccount: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Account", onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(SECTION_GAP),
        ) {
            if (state.message != null) {
                Text(
                    text = state.message,
                    color = StacksColors.Pine,
                    fontFamily = StacksType.Body,
                    fontSize = 13.sp,
                )
            }
            UpdateEmailSection(currentEmail = state.email, onUpdateEmail = onUpdateEmail)
            ChangePasswordSection(onChangePassword = onChangePassword)
            DangerZone(onDeleteAccount = onDeleteAccount)
        }
    }
}

/** `.sectit` — uppercase-style overline: small, muted, letter-spaced, semi-bold. */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = StacksColors.Muted,
        fontFamily = StacksType.Body,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 0.07.em,
    )
}

@Composable
private fun UpdateEmailSection(currentEmail: String, onUpdateEmail: (String) -> Unit) {
    var email by remember(currentEmail) { mutableStateOf(currentEmail) }
    Column(verticalArrangement = Arrangement.spacedBy(FIELD_GAP)) {
        SectionTitle("Email")
        AuthTextField(email, { email = it }, "Email")
        AuthPrimaryButton("Update email", enabled = email.isNotBlank(), onClick = { onUpdateEmail(email) })
    }
}

@Composable
private fun ChangePasswordSection(onChangePassword: (String, String) -> Unit) {
    var current by remember { mutableStateOf("") }
    var next by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(FIELD_GAP)) {
        SectionTitle("Password")
        AuthTextField(current, { current = it }, "Current password", isPassword = true)
        AuthTextField(next, { next = it }, "New password", isPassword = true)
        AuthPrimaryButton(
            text = "Change password",
            enabled = current.isNotBlank() && next.length >= MIN_PASSWORD,
            onClick = { onChangePassword(current, next) },
        )
    }
}

@Composable
private fun DangerZone(onDeleteAccount: () -> Unit) {
    var confirm by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(FIELD_GAP)) {
        SectionTitle("Danger zone")
        AuthErrorText("This permanently deletes your account and cannot be undone.")
        DangerButton("Delete account", onClick = { confirm = true })
    }
    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text("Delete account?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { confirm = false; onDeleteAccount() }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } },
        )
    }
}

/** `.btn.danger` — full-width destructive outline: surface fill, danger text + hairline border. */
@Composable
private fun DangerButton(text: String, onClick: () -> Unit) {
    val danger = MaterialTheme.colorScheme.error
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, danger.copy(alpha = 0.4f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = StacksColors.Surface,
            contentColor = danger,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text, fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}

private const val MIN_PASSWORD = 8
private val SECTION_GAP = 14.dp
private val FIELD_GAP = 6.dp

@Preview(showBackground = true, widthDp = 390, heightDp = 800)
@Composable
private fun AccountPreview() {
    LibraryTheme { AccountContent(AccountUiState(isLoading = false, email = "dana@stacks.app")) }
}
