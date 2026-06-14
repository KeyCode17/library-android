package com.library.android.presentation.screens.detail

import com.library.android.domain.model.Book

/** Single UiState for the book detail screen — exhaustive over its four modes. */
sealed interface BookDetailUiState {
    data object Loading : BookDetailUiState
    data class Content(val book: Book) : BookDetailUiState
    data object NotFound : BookDetailUiState
    data class Error(val message: String) : BookDetailUiState
}
