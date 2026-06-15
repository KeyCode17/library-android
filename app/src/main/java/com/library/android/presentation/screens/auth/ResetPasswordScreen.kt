package com.library.android.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.library.android.domain.repo.AuthRepository
import com.library.android.presentation.nav.Routes
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResetPasswordUiState(
    val isSubmitting: Boolean = false,
    val done: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    /** Token from the emailed deep link, if any; otherwise the user enters it manually. */
    val initialToken: String = savedStateHandle.get<String>(Routes.ARG_TOKEN).orEmpty()

    private val _state = MutableStateFlow(ResetPasswordUiState())
    val state: StateFlow<ResetPasswordUiState> = _state.asStateFlow()

    fun reset(token: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)
            authRepository.resetPassword(token, newPassword)
                .onSuccess { _state.value = ResetPasswordUiState(done = true) }
                .onFailure { _state.value = ResetPasswordUiState(error = it.message ?: "Reset failed") }
        }
    }
}

@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.done) { if (state.done) onDone() }
    ResetPasswordContent(
        state = state,
        initialToken = viewModel.initialToken,
        onSubmit = viewModel::reset,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

@Composable
fun ResetPasswordContent(
    state: ResetPasswordUiState,
    initialToken: String = "",
    onSubmit: (String, String) -> Unit = { _, _ -> },
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var token by remember(initialToken) { mutableStateOf(initialToken) }
    var password by remember { mutableStateOf("") }
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Set a new password", onBack)
        // .sub — own padding (0 24px 8px), sits outside the form's gap flow.
        Text(
            text = "Paste the code from your reset email and choose a new password.",
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 8.dp),
        )
        // .form — padding 8px 24px, gap 14px.
        Column(
            modifier = Modifier.padding(horizontal = 24.dp).padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AuthTextField(token, { token = it }, "Reset code")
            AuthTextField(password, { password = it }, "New password", isPassword = true)
            if (state.error != null) {
                AuthErrorText(state.error)
            }
            AuthPrimaryButton(
                text = "Reset password",
                enabled = !state.isSubmitting && token.isNotBlank() && password.length >= MIN_PASSWORD,
                onClick = { onSubmit(token, password) },
            )
            if (state.isSubmitting) {
                CircularProgressIndicator(color = StacksColors.Pine)
            }
        }
    }
}

private const val MIN_PASSWORD = 8

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ResetPasswordPreview() {
    LibraryTheme { ResetPasswordContent(ResetPasswordUiState(), initialToken = "abc123") }
}
