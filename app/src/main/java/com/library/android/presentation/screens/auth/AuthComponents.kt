package com.library.android.presentation.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType
import com.library.android.presentation.ui.StacksIcons

/** Back app bar for the auth screens ("‹ {title}"). */
@Composable
fun AuthTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 6.dp, end = 18.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = StacksIcons.NavBack,
            contentDescription = "Back",
            tint = StacksColors.Ink,
            modifier = Modifier.size(24.dp).clickable(onClick = onBack).padding(2.dp),
        )
        Text(
            text = title,
            color = StacksColors.Ink,
            fontFamily = StacksType.Display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
        )
    }
}

/** Single-line text field used for email/password across the auth screens. */
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation =
            if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

/** Full-width primary (pine) action button for the auth screens. */
@Composable
fun AuthPrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = StacksColors.Pine,
            contentColor = StacksColors.OnAccent,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(text, fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}

/** Inline error message for failed auth attempts. */
@Composable
fun AuthErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        fontFamily = StacksType.Body,
        fontSize = 13.sp,
    )
}
