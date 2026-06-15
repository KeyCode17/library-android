package com.library.android.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** Stateful register screen; on success the user is auto-logged-in and [onRegistered] fires. */
@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state) {
        if (state is AuthUiState.Authenticated) onRegistered()
    }
    RegisterContent(
        state = state,
        onSubmit = viewModel::register,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

/** Pure, previewable register form, conformed to docs/designs/register.html. */
@Composable
fun RegisterContent(
    state: AuthUiState,
    onSubmit: (String, String) -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loading = state is AuthUiState.Loading
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Create account", onBack)
        Column(
            modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Sign yourself up to browse the catalog and borrow books. " +
                    "Just an email and a password — at least 8 characters.",
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 14.sp,
            )
            AuthTextField(email, { email = it }, "Email")
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AuthTextField(password, { password = it }, "Password", isPassword = true)
                Text(
                    text = "Use 8 or more characters.",
                    color = StacksColors.Muted,
                    fontFamily = StacksType.Body,
                    fontSize = 13.sp,
                )
            }
            if (state is AuthUiState.Error) {
                AuthErrorText(state.message)
            }
            AuthPrimaryButton(
                text = "Create account",
                enabled = !loading && email.isNotBlank() && password.length >= MIN_PASSWORD,
                onClick = { onSubmit(email, password) },
            )
            if (loading) {
                CircularProgressIndicator(
                    color = StacksColors.Pine,
                    modifier = Modifier.testTag(AuthTestTags.LOADING),
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton(onClick = onBack) {
                    Text(
                        text = "Already have an account? Log in",
                        color = StacksColors.Pine,
                        fontFamily = StacksType.Body,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

private const val MIN_PASSWORD = 8

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun RegisterContentPreview() {
    LibraryTheme { RegisterContent(AuthUiState.Anonymous, onSubmit = { _, _ -> }) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun RegisterErrorPreview() {
    LibraryTheme {
        RegisterContent(AuthUiState.Error("Email already in use"), onSubmit = { _, _ -> })
    }
}
