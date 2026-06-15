package com.library.android.presentation.screens.detail

import com.library.android.domain.model.Book

/** Single UiState for the book detail screen — exhaustive over its four modes. */
sealed interface BookDetailUiState {
    data object Loading : BookDetailUiState
    data class Content(val book: Book) : BookDetailUiState
    data object NotFound : BookDetailUiState
    data class Error(val message: String) : BookDetailUiState
}

/**
 * Transient state for the detail Borrow action, kept separate so the load UiState is untouched.
 * [requireLogin] is a one-shot flag the screen consumes to send an anonymous user to sign-in.
 */
data class BorrowUiState(
    val inProgress: Boolean = false,
    val message: String? = null,
    val requireLogin: Boolean = false,
)
