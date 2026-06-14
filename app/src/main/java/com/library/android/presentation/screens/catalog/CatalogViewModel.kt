package com.library.android.presentation.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.usecase.GetBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns the catalog screen state and the active book-finder filter. Private [MutableStateFlow]s
 * in, read-only [StateFlow]s out (UDF). Reloads whenever the filter changes.
 */
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getBooks: GetBooksUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    private val _filter = MutableStateFlow(CatalogFilter())
    val filter: StateFlow<CatalogFilter> = _filter.asStateFlow()

    init {
        load()
    }

    /** Applies a shelf/row finder filter (blank shelf clears that field) and reloads. */
    fun applyFinder(shelf: String?, row: Int?) {
        _filter.value = CatalogFilter(shelf = shelf?.ifBlank { null }, row = row)
        load()
    }

    /** Clears the finder filter and reloads the full catalog. */
    fun clearFinder() {
        _filter.value = CatalogFilter()
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = CatalogUiState.Loading
            val activeFilter = _filter.value
            getBooks(activeFilter.shelf, activeFilter.row)
                .onSuccess { books ->
                    _state.value = if (books.isEmpty()) {
                        CatalogUiState.Empty
                    } else {
                        CatalogUiState.Content(books)
                    }
                }
                .onFailure { error ->
                    _state.value = CatalogUiState.Error(error.message ?: DEFAULT_ERROR)
                }
        }
    }

    private companion object {
        const val DEFAULT_ERROR = "Couldn't load the catalog"
    }
}
