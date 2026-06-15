package com.library.android.presentation.screens.wifi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/** Pure, previewable WiFi UI — mirrors `docs/designs/wifi.html`. */
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
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NetworkCard(
                networkName = networkName,
                connectEnabled = state != WifiUiState.Provisioning &&
                    state != WifiUiState.Unsupported,
                provisioning = state == WifiUiState.Provisioning,
                onProvision = onProvision,
            )
            Text(
                text = "On Android 9 and older (API < 29) the app falls back gracefully — the " +
                    "network is offered as a saved Suggested entry instead of being added directly.",
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 13.sp,
            )
            StatusRow(state)
        }
    }
}

/** Centered network card: wifi glyph, SSID, helper, and the Connect action. */
@Composable
private fun NetworkCard(
    networkName: String,
    connectEnabled: Boolean,
    provisioning: Boolean,
    onProvision: () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(StacksColors.Surface)
            .border(1.dp, StacksColors.Line, shape)
            .padding(horizontal = 18.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = WifiGlyph,
            contentDescription = null,
            tint = StacksColors.Pine,
            modifier = Modifier.size(56.dp).padding(bottom = 8.dp),
        )
        Text(
            text = networkName,
            color = StacksColors.Ink,
            fontFamily = StacksType.Display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )
        Text(
            text = "Tap to join the library network",
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onProvision,
            enabled = connectEnabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = StacksColors.Pine,
                contentColor = StacksColors.OnAccent,
            ),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        ) {
            Text(
                text = "Connect",
                fontFamily = StacksType.Body,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            )
        }
        if (provisioning) {
            CircularProgressIndicator(
                color = StacksColors.Pine,
                modifier = Modifier.padding(top = 12.dp).size(20.dp),
            )
        }
    }
}

/** "Status" label plus a state-driven badge (mirrors `.rowflex` + `.badge`). */
@Composable
private fun StatusRow(state: WifiUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Status",
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
        StatusBadge(state)
    }
}

/** Pill badge whose label + colour track the provisioning state. */
@Composable
private fun StatusBadge(state: WifiUiState) {
    val (label, fg, bg) = when (state) {
        WifiUiState.Idle ->
            Triple("Ready", StacksColors.Pine, StacksColors.Sage100)
        WifiUiState.Provisioning ->
            Triple("Adding…", StacksColors.Faint, StacksColors.OutChipBg)
        WifiUiState.Success ->
            Triple("Suggested", StacksColors.Pine, StacksColors.Sage100)
        WifiUiState.Unsupported ->
            Triple("Unsupported", StacksColors.Brass700, StacksColors.Brass100)
        is WifiUiState.Error ->
            Triple("Error", StacksColors.Brass700, StacksColors.Brass100)
    }
    val shape = RoundedCornerShape(999.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(bg)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            color = fg,
            fontFamily = StacksType.Body,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
        )
    }
    // Full provisioning detail (test-asserted copy) sits below the badge row.
    StatusMessage(state)
}

@Composable
private fun StatusMessage(state: WifiUiState) {
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
        fontSize = 13.sp,
    )
}

/**
 * Wifi glyph translated 1:1 from the SVG path data in `docs/designs/wifi.html` so the
 * on-device card matches the design exactly. Drawn black; recoloured at the use-site via tint.
 */
private val WifiGlyph: ImageVector = ImageVector.Builder("WifiGlyph", 24.dp, 24.dp, 24f, 24f)
    .addPath(
        pathData = addPathNodes("M2 8.5a16 16 0 0120 0 M5 12a11 11 0 0114 0 M8.5 15.5a6 6 0 017 0"),
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    .addPath(
        pathData = addPathNodes("M12 17.9a1.1 1.1 0 100 2.2 1.1 1.1 0 000-2.2z"),
        fill = SolidColor(Color.Black),
    )
    .build()

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
