package com.library.android.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

// Circled check from the design's verified-state SVG (circle r9 + check path, stroke-width 2).
// Drawn locally because the shared StacksIcons set has no check glyph and is not editable here.
private val VerifiedCheck: ImageVector =
    ImageVector.Builder("VerifiedCheck", 24.dp, 24.dp, 24f, 24f).addPath(
        pathData = addPathNodes("M3 12a9 9 0 1018 0 9 9 0 10-18 0z M8.5 12.5l2.5 2.5 4.5-5"),
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ).build()

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
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Verify email", onBack)
        when (state) {
            VerifyEmailUiState.Verified -> VerifiedState()
            VerifyEmailUiState.Verifying -> VerifyingState()
            else -> ManualEntry(
                state = state,
                initialToken = initialToken,
                onVerify = onVerify,
            )
        }
    }
}

/** Centered confirmation — mirrors the design's `.empty` state (pine check + h2 thanks). */
@Composable
private fun VerifiedState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = VerifiedCheck,
            contentDescription = null,
            tint = StacksColors.Pine,
            modifier = Modifier.size(44.dp).padding(bottom = 8.dp),
        )
        Text(
            text = "Your email is verified. Thanks!",
            color = StacksColors.Ink,
            fontFamily = StacksType.Display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun VerifyingState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = StacksColors.Pine)
    }
}

/** Manual code entry — sub text + "Verification code" field + "Verify" button. */
@Composable
private fun ManualEntry(
    state: VerifyEmailUiState,
    initialToken: String,
    onVerify: (String) -> Unit,
) {
    var token by remember(initialToken) { mutableStateOf(initialToken) }
    Column(
        modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
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

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun VerifyEmailPreview() {
    LibraryTheme { VerifyEmailContent(VerifyEmailUiState.Idle) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun VerifyEmailVerifiedPreview() {
    LibraryTheme { VerifyEmailContent(VerifyEmailUiState.Verified) }
}
