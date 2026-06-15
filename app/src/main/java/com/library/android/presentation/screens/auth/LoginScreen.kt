package com.library.android.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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

/** Stateful login screen; navigates away via [onLoggedIn] once authenticated. */
@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPassword: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state) {
        if (state is AuthUiState.Authenticated) onLoggedIn()
    }
    LoginContent(
        state = state,
        onSubmit = viewModel::login,
        onRegisterClick = onRegisterClick,
        onForgotPassword = onForgotPassword,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

/** Pure, previewable login form. Clean default — auth screens need a design pass. */
@Composable
fun LoginContent(
    state: AuthUiState,
    onSubmit: (String, String) -> Unit,
    onRegisterClick: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loading = state is AuthUiState.Loading
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Welcome back", onBack)
        Column(
            modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Sign in to your library account",
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 14.sp,
            )
            AuthTextField(email, { email = it }, "Email")
            AuthTextField(password, { password = it }, "Password", isPassword = true)
            if (state is AuthUiState.Error) {
                AuthErrorText(state.message)
            }
            AuthPrimaryButton(
                text = "Log in",
                enabled = !loading && email.isNotBlank() && password.isNotBlank(),
                onClick = { onSubmit(email, password) },
            )
            if (loading) {
                CircularProgressIndicator(
                    color = StacksColors.Pine,
                    modifier = Modifier.testTag(AuthTestTags.LOADING),
                )
            }
            TextButton(onClick = onRegisterClick) {
                Text(
                    text = "New here? Create an account",
                    color = StacksColors.Pine,
                    fontFamily = StacksType.Body,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
            TextButton(onClick = onForgotPassword) {
                Text(
                    text = "Forgot password?",
                    color = StacksColors.Pine,
                    fontFamily = StacksType.Body,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun LoginContentPreview() {
    LibraryTheme { LoginContent(AuthUiState.Anonymous, onSubmit = { _, _ -> }) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun LoginErrorPreview() {
    LibraryTheme {
        LoginContent(AuthUiState.Error("Invalid email or password"), onSubmit = { _, _ -> })
    }
}
