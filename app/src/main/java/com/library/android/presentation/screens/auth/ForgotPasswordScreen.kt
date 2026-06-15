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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.library.android.domain.repo.AuthRepository
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ForgotPasswordUiState {
    data object Idle : ForgotPasswordUiState
    data object Submitting : ForgotPasswordUiState
    data object Sent : ForgotPasswordUiState
    data class Error(val message: String) : ForgotPasswordUiState
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val state: StateFlow<ForgotPasswordUiState> = _state.asStateFlow()

    fun submit(email: String) {
        viewModelScope.launch {
            _state.value = ForgotPasswordUiState.Submitting
            authRepository.forgotPassword(email)
                .onSuccess { _state.value = ForgotPasswordUiState.Sent }
                .onFailure {
                    _state.value = ForgotPasswordUiState.Error(it.message ?: "Couldn't send the email")
                }
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onProceedToReset: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ForgotPasswordContent(
        state = state,
        onSubmit = viewModel::submit,
        onBack = onBack,
        onProceedToReset = onProceedToReset,
        modifier = Modifier.systemBarsPadding(),
    )
}

@Composable
fun ForgotPasswordContent(
    state: ForgotPasswordUiState,
    onSubmit: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onProceedToReset: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("") }
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Reset password", onBack)
        Column(
            modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (state is ForgotPasswordUiState.Sent) {
                Text(
                    text = "If an account exists for that email, we've sent a reset link. " +
                        "Open it on this device, or enter the code from the email.",
                    color = StacksColors.Muted,
                    fontFamily = StacksType.Body,
                    fontSize = 14.sp,
                )
                AuthPrimaryButton("Enter reset code", enabled = true, onClick = onProceedToReset)
            } else {
                Text(
                    text = "Enter your email and we'll send a password-reset link.",
                    color = StacksColors.Muted,
                    fontFamily = StacksType.Body,
                    fontSize = 14.sp,
                )
                AuthTextField(email, { email = it }, "Email")
                if (state is ForgotPasswordUiState.Error) {
                    AuthErrorText(state.message)
                }
                AuthPrimaryButton(
                    text = "Send reset link",
                    enabled = state !is ForgotPasswordUiState.Submitting && email.isNotBlank(),
                    onClick = { onSubmit(email) },
                )
                if (state is ForgotPasswordUiState.Submitting) {
                    CircularProgressIndicator(color = StacksColors.Pine)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ForgotPasswordSentPreview() {
    LibraryTheme { ForgotPasswordContent(ForgotPasswordUiState.Sent) }
}
