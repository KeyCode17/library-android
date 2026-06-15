package com.library.android.presentation.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.library.android.domain.model.Role
import com.library.android.domain.model.UserSummary
import com.library.android.presentation.screens.auth.AuthPrimaryButton
import com.library.android.presentation.screens.auth.AuthTextField
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** A user row: email + badges + role chips + activate/deactivate + delete. */
@Composable
fun UserRow(
    user: UserSummary,
    onAssignRole: (Role) -> Unit,
    onToggleActive: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(StacksColors.Surface)
            .border(1.dp, StacksColors.Line, shape)
            .padding(14.dp),
    ) {
        Text(
            text = user.email,
            color = StacksColors.Ink,
            fontFamily = StacksType.Display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        UserBadges(user)
        Spacer(Modifier.height(10.dp))
        RoleChips(current = user.role, onAssignRole = onAssignRole)
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { onToggleActive(!user.active) }) {
                Text(
                    text = if (user.active) "Deactivate" else "Activate",
                    color = StacksColors.Pine,
                    fontFamily = StacksType.Body,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
            TextButton(onClick = onDelete) {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = StacksType.Body,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun UserBadges(user: UserSummary) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Badge(user.role.name.lowercase(), StacksColors.Brass700, StacksColors.Brass100)
        if (user.verified) Badge("verified", StacksColors.Pine, StacksColors.Sage100)
        if (!user.active) Badge("inactive", StacksColors.Faint, StacksColors.OutChipBg)
    }
}

@Composable
private fun Badge(label: String, foreground: Color, background: Color) {
    Text(
        text = label,
        color = foreground,
        fontFamily = StacksType.Body,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    )
}

/** Role selector: tap a role to assign it (current role highlighted). */
@Composable
fun RoleChips(current: Role, onAssignRole: (Role) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Role.entries.forEach { role ->
            val selected = role == current
            Text(
                text = role.name.lowercase(),
                color = if (selected) StacksColors.OnAccent else StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (selected) StacksColors.Pine else StacksColors.Surface)
                    .border(1.dp, if (selected) StacksColors.Pine else StacksColors.Line, RoundedCornerShape(999.dp))
                    .clickable(enabled = !selected) { onAssignRole(role) }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
    }
}

/** "Create user" card: email + password + role chips + submit. */
@Composable
fun CreateUserForm(onCreate: (String, String, Role) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Role.MEMBER) }
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(StacksColors.Surface)
            .border(1.dp, StacksColors.Line, shape)
            .padding(14.dp),
    ) {
        Text(
            text = "New user",
            color = StacksColors.Ink,
            fontFamily = StacksType.Display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )
        Spacer(Modifier.height(12.dp))
        AuthTextField(email, { email = it }, "Email")
        Spacer(Modifier.height(10.dp))
        AuthTextField(password, { password = it }, "Password", isPassword = true)
        Spacer(Modifier.height(12.dp))
        RoleChips(current = role, onAssignRole = { role = it })
        Spacer(Modifier.height(14.dp))
        AuthPrimaryButton(
            text = "Create user",
            enabled = email.isNotBlank() && password.length >= MIN_PASSWORD,
            onClick = { onCreate(email, password, role) },
        )
    }
}

private const val MIN_PASSWORD = 8
