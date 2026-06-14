package com.library.android.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.android.domain.model.Principal
import com.library.android.domain.model.Role
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/**
 * Authenticated-only profile screen (gating demo): shows the `/auth/me` principal when signed
 * in, otherwise a sign-in prompt. Reachable from the catalog's Profile entry; catalog stays
 * public.
 */
@Composable
fun ProfileScreen(
    onLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProfileContent(
        state = state,
        onLogout = viewModel::logout,
        onLogin = onLogin,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

/** Pure, previewable profile UI. */
@Composable
fun ProfileContent(
    state: AuthUiState,
    onLogout: () -> Unit = {},
    onLogin: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Profile", onBack)
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                AuthUiState.Loading -> CircularProgressIndicator(
                    color = StacksColors.Pine,
                    modifier = Modifier.testTag(AuthTestTags.LOADING),
                )
                is AuthUiState.Authenticated -> AuthenticatedProfile(state.principal, onLogout)
                AuthUiState.Anonymous -> SignedOutPrompt(onLogin)
                is AuthUiState.Error -> SignedOutPrompt(onLogin)
            }
        }
    }
}

@Composable
private fun AuthenticatedProfile(principal: Principal, onLogout: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Signed in as",
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontSize = 13.sp,
        )
        Text(
            text = principal.email,
            color = StacksColors.Ink,
            fontFamily = StacksType.Display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Role: ${roleLabel(principal.role)}",
            color = StacksColors.Muted,
            fontFamily = StacksType.Mono,
            fontSize = 13.sp,
        )
        AuthPrimaryButton(
            text = "Log out",
            enabled = true,
            onClick = onLogout,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun SignedOutPrompt(onLogin: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "You're signed out",
            color = StacksColors.Ink,
            fontFamily = StacksType.Display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Sign in to view your profile.",
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
        AuthPrimaryButton(
            text = "Log in",
            enabled = true,
            onClick = onLogin,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

private fun roleLabel(role: Role): String =
    role.name.lowercase().replaceFirstChar { it.uppercase() }

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ProfileAuthenticatedPreview() {
    LibraryTheme {
        ProfileContent(
            AuthUiState.Authenticated(Principal("1", "dana@stacks.app", Role.MEMBER)),
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ProfileSignedOutPreview() {
    LibraryTheme { ProfileContent(AuthUiState.Anonymous) }
}
