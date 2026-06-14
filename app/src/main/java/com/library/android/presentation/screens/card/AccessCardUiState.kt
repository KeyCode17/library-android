package com.library.android.presentation.screens.card

import androidx.compose.runtime.Immutable
import com.library.android.domain.device.QrMatrix

/** UiState for the QR access card. */
sealed interface AccessCardUiState {
    data object Loading : AccessCardUiState
    data object Anonymous : AccessCardUiState
    @Immutable
    data class Ready(val membershipId: String, val email: String, val qr: QrMatrix) : AccessCardUiState
    data class Error(val message: String) : AccessCardUiState
}
