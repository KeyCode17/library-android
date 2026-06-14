package com.library.android.presentation.screens.wifi

/** UiState for library WiFi provisioning. */
sealed interface WifiUiState {
    data object Idle : WifiUiState
    data object Provisioning : WifiUiState
    data object Success : WifiUiState
    data object Unsupported : WifiUiState
    data class Error(val message: String) : WifiUiState
}
