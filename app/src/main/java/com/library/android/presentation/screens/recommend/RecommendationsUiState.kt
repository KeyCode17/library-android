package com.library.android.presentation.screens.recommend

import com.library.android.domain.model.Book

/** UiState for the on-device recommendations screen. */
sealed interface RecommendationsUiState {
    data object Idle : RecommendationsUiState
    data object Loading : RecommendationsUiState
    data object Empty : RecommendationsUiState
    data class Error(val message: String) : RecommendationsUiState
    data class Loaded(val books: List<Book>) : RecommendationsUiState
}
