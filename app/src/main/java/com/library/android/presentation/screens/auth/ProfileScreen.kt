@file:Suppress("TooManyFunctions") // Compose screen: many small, previewable composables

package com.library.android.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
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
    actions: ProfileActions,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProfileContent(
        state = state,
        onLogout = viewModel::logout,
        onLogin = onLogin,
        onBack = onBack,
        actions = actions,
        modifier = Modifier.systemBarsPadding(),
    )
}

/** The authenticated-area navigation actions, grouped to keep the profile signature small. */
data class ProfileActions(
    val onReminders: () -> Unit = {},
    val onAccessCard: () -> Unit = {},
    val onWifi: () -> Unit = {},
    val onAccount: () -> Unit = {},
    val onManageUsers: () -> Unit = {},
    val onVerifyEmail: () -> Unit = {},
)

/** Pure, previewable profile UI. */
@Composable
fun ProfileContent(
    state: AuthUiState,
    onLogout: () -> Unit = {},
    onLogin: () -> Unit = {},
    onBack: () -> Unit = {},
    actions: ProfileActions = ProfileActions(),
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Profile", onBack)
        when (state) {
            is AuthUiState.Authenticated ->
                AuthenticatedProfile(state.principal, onLogout, actions)
            // Loading + signed-out states are vertically centered (design `.empty`).
            else -> Box(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (state is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        color = StacksColors.Pine,
                        modifier = Modifier.testTag(AuthTestTags.LOADING),
                    )
                } else {
                    SignedOutPrompt(onLogin)
                }
            }
        }
    }
}

@Composable
private fun AuthenticatedProfile(
    principal: Principal,
    onLogout: () -> Unit,
    actions: ProfileActions,
) {
    // Design: top-aligned, scrollable; identity block (padding-top 32px) over the nav stack.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        IdentityBlock(principal)
        if (!principal.verified) {
            UnverifiedBanner(onVerify = actions.onVerifyEmail)
        }
        NavStack(principal, onLogout, actions)
    }
}

/** "Signed in as / {email} / Role: {role}" identity header (design `.empty` flex-start). */
@Composable
private fun IdentityBlock(principal: Principal) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Signed in as",
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontSize = 14.sp,
        )
        Text(
            text = principal.email,
            color = StacksColors.Ink,
            fontFamily = StacksType.Display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Role: ${roleLabel(principal.role)}",
            color = StacksColors.Muted,
            fontFamily = StacksType.Mono,
            fontSize = 14.sp,
        )
    }
}

/** Full-width nav button stack; admins also get "Manage users" (design `.form` gap 10px). */
@Composable
private fun NavStack(
    principal: Principal,
    onLogout: () -> Unit,
    actions: ProfileActions,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AuthPrimaryButton(text = "Reminders", enabled = true, onClick = actions.onReminders)
        AuthPrimaryButton(text = "Access card", enabled = true, onClick = actions.onAccessCard)
        AuthPrimaryButton(text = "Library WiFi", enabled = true, onClick = actions.onWifi)
        AuthPrimaryButton(text = "Account settings", enabled = true, onClick = actions.onAccount)
        if (principal.role == Role.ADMIN) {
            AuthPrimaryButton(text = "Manage users", enabled = true, onClick = actions.onManageUsers)
        }
        AuthPrimaryButton(text = "Log out", enabled = true, onClick = onLogout)
    }
}

/** Banner shown when the signed-in user's email is unverified (design `.banner`). */
@Composable
private fun UnverifiedBanner(onVerify: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(StacksColors.Brass100)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Please verify your email to secure your account.",
            color = StacksColors.Brass700,
            fontFamily = StacksType.Body,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        AuthPrimaryButton(text = "Verify email", enabled = true, onClick = onVerify)
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
            modifier = Modifier.padding(top = 8.dp),
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
            AuthUiState.Authenticated(
                Principal("1", "dana@stacks.app", Role.MEMBER, verified = true, active = true),
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ProfileUnverifiedPreview() {
    LibraryTheme {
        ProfileContent(
            AuthUiState.Authenticated(
                Principal("1", "dana@stacks.app", Role.MEMBER, verified = false, active = true),
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ProfileSignedOutPreview() {
    LibraryTheme { ProfileContent(AuthUiState.Anonymous) }
}
