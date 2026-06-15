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

sealed interface VerifyEmailUiState {
    data object Idle : VerifyEmailUiState
    data object Verifying : VerifyEmailUiState
    data object Verified : VerifyEmailUiState
    data class Error(val message: String) : VerifyEmailUiState
}

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val initialToken: String = savedStateHandle.get<String>(Routes.ARG_TOKEN).orEmpty()

    private val _state = MutableStateFlow<VerifyEmailUiState>(VerifyEmailUiState.Idle)
    val state: StateFlow<VerifyEmailUiState> = _state.asStateFlow()

    init {
        // Auto-verify when arriving via the emailed deep link.
        if (initialToken.isNotBlank()) verify(initialToken)
    }

    fun verify(token: String) {
        viewModelScope.launch {
            _state.value = VerifyEmailUiState.Verifying
            authRepository.verifyEmail(token)
                .onSuccess { _state.value = VerifyEmailUiState.Verified }
                .onFailure {
                    _state.value = VerifyEmailUiState.Error(it.message ?: "Verification failed")
                }
        }
    }
}

@Composable
fun VerifyEmailScreen(
    onBack: () -> Unit,
    viewModel: VerifyEmailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    VerifyEmailContent(
        state = state,
        initialToken = viewModel.initialToken,
        onVerify = viewModel::verify,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

@Composable
fun VerifyEmailContent(
    state: VerifyEmailUiState,
    initialToken: String = "",
    onVerify: (String) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var token by remember(initialToken) { mutableStateOf(initialToken) }
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Verify email", onBack)
        Column(
            modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            when (state) {
                VerifyEmailUiState.Verified -> Text(
                    text = "Your email is verified. Thanks!",
                    color = StacksColors.Ink,
                    fontFamily = StacksType.Display,
                    fontSize = 18.sp,
                )
                VerifyEmailUiState.Verifying -> CircularProgressIndicator(color = StacksColors.Pine)
                else -> {
                    Text(
                        text = "Paste the code from your verification email.",
                        color = StacksColors.Muted,
                        fontFamily = StacksType.Body,
                        fontSize = 14.sp,
                    )
                    AuthTextField(token, { token = it }, "Verification code")
                    if (state is VerifyEmailUiState.Error) {
                        AuthErrorText(state.message)
                    }
                    AuthPrimaryButton(
                        text = "Verify",
                        enabled = token.isNotBlank(),
                        onClick = { onVerify(token) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun VerifyEmailPreview() {
    LibraryTheme { VerifyEmailContent(VerifyEmailUiState.Idle) }
}
