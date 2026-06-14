package com.library.android.presentation.screens.reminders

import com.library.android.domain.model.Reminder

/** Reminders screen UiState (history + load/auth facets). */
data class RemindersUiState(
    val isLoading: Boolean = false,
    val isAnonymous: Boolean = false,
    val reminders: List<Reminder> = emptyList(),
    val error: String? = null,
)
