package com.library.android.presentation.screens.reminders

import androidx.compose.runtime.Immutable
import com.library.android.domain.model.Reminder

/** Reminders screen UiState (history + load/auth facets). */
@Immutable
data class RemindersUiState(
    val isLoading: Boolean = false,
    val isAnonymous: Boolean = false,
    val reminders: List<Reminder> = emptyList(),
    val error: String? = null,
)
