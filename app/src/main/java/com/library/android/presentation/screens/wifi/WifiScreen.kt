package com.library.android.presentation.screens.wifi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.android.presentation.screens.auth.AuthTopBar
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** Stateful library-WiFi screen (reached from Profile). */
@Composable
fun WifiScreen(
    onBack: () -> Unit,
    viewModel: WifiViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    WifiContent(
        state = state,
        networkName = LIBRARY_WIFI_SSID,
        onProvision = viewModel::provision,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

/** Pure, previewable WiFi UI. */
@Composable
fun WifiContent(
    state: WifiUiState,
    networkName: String = LIBRARY_WIFI_SSID,
    onProvision: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Library WiFi", onBack)
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Network",
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 13.sp,
            )
            Text(
                text = networkName,
                color = StacksColors.Ink,
                fontFamily = StacksType.Display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
            )
            StatusText(state)
            Button(
                onClick = onProvision,
                enabled = state != WifiUiState.Provisioning && state != WifiUiState.Unsupported,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StacksColors.Pine,
                    contentColor = StacksColors.OnAccent,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Connect", fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            }
            if (state == WifiUiState.Provisioning) {
                CircularProgressIndicator(color = StacksColors.Pine)
            }
        }
    }
}

@Composable
private fun StatusText(state: WifiUiState) {
    val message = when (state) {
        WifiUiState.Idle -> "Add the library network to this device."
        WifiUiState.Provisioning -> "Adding the network…"
        WifiUiState.Success -> "Network added — your device may connect automatically."
        WifiUiState.Unsupported -> "WiFi provisioning needs Android 10 or newer."
        is WifiUiState.Error -> state.message
    }
    Text(
        text = message,
        color = StacksColors.Muted,
        fontFamily = StacksType.Body,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
    )
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun WifiIdlePreview() {
    LibraryTheme { WifiContent(WifiUiState.Idle) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun WifiSuccessPreview() {
    LibraryTheme { WifiContent(WifiUiState.Success) }
}
