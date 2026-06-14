package com.library.android.presentation.screens.lending

import androidx.compose.runtime.Immutable
import com.library.android.domain.model.Loan

/**
 * Lending UiState. A flat data class (loading + data + role + transient message) fits better
 * than a sealed type here since several facets vary independently.
 */
@Immutable
data class LendingUiState(
    val isLoading: Boolean = false,
    val isAnonymous: Boolean = false,
    val isStaff: Boolean = false,
    val loans: List<Loan> = emptyList(),
    val message: String? = null,
)
