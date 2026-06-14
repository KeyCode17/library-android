package com.library.android.presentation.screens.card

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.android.domain.device.QrMatrix
import com.library.android.presentation.screens.auth.AuthTopBar
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** Stateful access-card screen (reached from Profile; gated by auth). */
@Composable
fun AccessCardScreen(
    onLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: AccessCardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AccessCardContent(state = state, onLogin = onLogin, onBack = onBack, modifier = Modifier.systemBarsPadding())
}

/** Pure, previewable access-card UI. */
@Composable
fun AccessCardContent(
    state: AccessCardUiState,
    onLogin: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Access card", onBack)
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            when (state) {
                AccessCardUiState.Loading -> CircularProgressIndicator(
                    color = StacksColors.Pine,
                    modifier = Modifier.testTag(AccessCardTestTags.LOADING),
                )
                AccessCardUiState.Anonymous -> SignInGate(onLogin)
                is AccessCardUiState.Error -> CenteredMessage(state.message)
                is AccessCardUiState.Ready -> AccessCard(state)
            }
        }
    }
}

@Composable
private fun AccessCard(state: AccessCardUiState.Ready) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(StacksColors.Surface)
            .border(1.dp, StacksColors.Line, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Library access card",
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
        )
        QrImage(state.qr, Modifier.size(220.dp).background(Color.White).testTag(AccessCardTestTags.QR))
        Text(
            text = state.email,
            color = StacksColors.Ink,
            fontFamily = StacksType.Display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "ID ${state.membershipId}",
            color = StacksColors.Muted,
            fontFamily = StacksType.Mono,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun QrImage(matrix: QrMatrix, modifier: Modifier) {
    // The QR is meaningful content (not decorative), so it carries a TalkBack description.
    Canvas(modifier.semantics { contentDescription = "Library access card QR code" }) {
        val module = size.minDimension / matrix.size
        for (y in 0 until matrix.size) {
            for (x in 0 until matrix.size) {
                if (matrix.isDark(x, y)) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(x * module, y * module),
                        size = Size(module, module),
                    )
                }
            }
        }
    }
}

@Composable
private fun SignInGate(onLogin: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Sign in to show your access card",
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

@Composable
private fun CenteredMessage(text: String) {
    Text(
        text = text,
        color = StacksColors.Muted,
        fontFamily = StacksType.Body,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
    )
}

private val sampleMatrix = QrMatrix(
    size = 3,
    modules = booleanArrayOf(true, false, true, false, true, false, true, false, true),
)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AccessCardReadyPreview() {
    LibraryTheme {
        AccessCardContent(AccessCardUiState.Ready("u-123", "dana@stacks.app", sampleMatrix))
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AccessCardSignedOutPreview() {
    LibraryTheme { AccessCardContent(AccessCardUiState.Anonymous) }
}
