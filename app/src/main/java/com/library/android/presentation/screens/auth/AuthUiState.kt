package com.library.android.presentation.screens.auth

import com.library.android.domain.model.Principal

/** Single auth UiState shared by the login, register and profile screens. */
sealed interface AuthUiState {
    data object Anonymous : AuthUiState
    data object Loading : AuthUiState
    data class Authenticated(val principal: Principal) : AuthUiState
    data class Error(val message: String) : AuthUiState
}
