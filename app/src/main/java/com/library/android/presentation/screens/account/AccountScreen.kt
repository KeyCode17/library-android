package com.library.android.presentation.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (state.message != null) {
                Text(state.message, color = StacksColors.Pine, fontFamily = StacksType.Body, fontSize = 13.sp)
            }
            UpdateEmailSection(currentEmail = state.email, onUpdateEmail = onUpdateEmail)
            ChangePasswordSection(onChangePassword = onChangePassword)
            DangerZone(onDeleteAccount = onDeleteAccount)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = StacksColors.Ink,
        fontFamily = StacksType.Display,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    )
}

@Composable
private fun UpdateEmailSection(currentEmail: String, onUpdateEmail: (String) -> Unit) {
    var email by remember(currentEmail) { mutableStateOf(currentEmail) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle("Email")
        AuthTextField(email, { email = it }, "Email")
        AuthPrimaryButton("Update email", enabled = email.isNotBlank(), onClick = { onUpdateEmail(email) })
    }
}

@Composable
private fun ChangePasswordSection(onChangePassword: (String, String) -> Unit) {
    var current by remember { mutableStateOf("") }
    var next by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle("Danger zone")
        AuthErrorText("This permanently deletes your account and cannot be undone.")
        AuthPrimaryButton("Delete account", enabled = true, onClick = { confirm = true })
    }
    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text("Delete account?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { confirm = false; onDeleteAccount() }) {
                    Text("Delete", color = StacksColors.Pine)
                }
            },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } },
        )
    }
}

private const val MIN_PASSWORD = 8

@Preview(showBackground = true, widthDp = 390, heightDp = 800)
@Composable
private fun AccountPreview() {
    LibraryTheme { AccountContent(AccountUiState(isLoading = false, email = "dana@stacks.app")) }
}
