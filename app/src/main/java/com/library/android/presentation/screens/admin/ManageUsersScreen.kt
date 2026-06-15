package com.library.android.presentation.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.android.domain.model.Role
import com.library.android.domain.model.UserSummary
import com.library.android.presentation.screens.auth.AuthTopBar
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** Stateful admin Manage Users screen. */
@Composable
fun ManageUsersScreen(
    onBack: () -> Unit,
    viewModel: ManageUsersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ManageUsersContent(
        state = state,
        onAssignRole = viewModel::assignRole,
        onCreate = viewModel::createUser,
        onToggleActive = viewModel::setActive,
        onDelete = viewModel::deleteUser,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

@Composable
fun ManageUsersContent(
    state: ManageUsersUiState,
    onAssignRole: (String, Role) -> Unit = { _, _ -> },
    onCreate: (String, String, Role) -> Unit = { _, _, _ -> },
    onToggleActive: (String, Boolean) -> Unit = { _, _ -> },
    onDelete: (String) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar("Manage users", onBack)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when {
                state.isLoading -> Centered { CircularProgressIndicator(color = StacksColors.Pine) }
                state.isForbidden -> Centered {
                    Text(
                        text = "Admin access required",
                        color = StacksColors.Muted,
                        fontFamily = StacksType.Body,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                else -> UsersList(state, onAssignRole, onCreate, onToggleActive, onDelete)
            }
        }
    }
}

@Composable
private fun UsersList(
    state: ManageUsersUiState,
    onAssignRole: (String, Role) -> Unit,
    onCreate: (String, String, Role) -> Unit,
    onToggleActive: (String, Boolean) -> Unit,
    onDelete: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { CreateUserForm(onCreate = onCreate) }
        state.message?.let { message ->
            item {
                Text(message, color = StacksColors.Pine, fontFamily = StacksType.Body, fontSize = 13.sp)
            }
        }
        items(state.users, key = { it.id }) { user ->
            UserRow(
                user = user,
                onAssignRole = { role -> onAssignRole(user.id, role) },
                onToggleActive = { active -> onToggleActive(user.id, active) },
                onDelete = { onDelete(user.id) },
            )
        }
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { content() }
}

private val sampleUsers = listOf(
    UserSummary("1", "ada@stacks.app", Role.ADMIN, verified = true, active = true, createdAt = "2026-01-01T00:00:00Z"),
    UserSummary(
        "2", "mem@stacks.app", Role.MEMBER, verified = false, active = true,
        createdAt = "2026-02-01T00:00:00Z",
    ),
)

@Preview(showBackground = true, widthDp = 390, heightDp = 800)
@Composable
private fun ManageUsersPreview() {
    LibraryTheme { ManageUsersContent(ManageUsersUiState(isLoading = false, users = sampleUsers)) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ManageUsersForbiddenPreview() {
    LibraryTheme { ManageUsersContent(ManageUsersUiState(isLoading = false, isForbidden = true)) }
}
