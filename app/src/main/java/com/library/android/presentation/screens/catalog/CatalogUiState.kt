package com.library.android.presentation.screens.catalog

import androidx.compose.runtime.Immutable
import com.library.android.domain.model.Book

/**
 * Single UiState for the catalog list — a sealed interface so `when` is exhaustive over the
 * four mutually-exclusive screen modes (the discriminated-union shape from the skill).
 */
sealed interface CatalogUiState {
    data object Loading : CatalogUiState
    @Immutable
    data class Content(val books: List<Book>) : CatalogUiState
    data object Empty : CatalogUiState
    data class Error(val message: String) : CatalogUiState
}
