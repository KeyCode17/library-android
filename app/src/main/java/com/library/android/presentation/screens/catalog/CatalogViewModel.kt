package com.library.android.presentation.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.usecase.GetBooksUseCase
import com.library.android.domain.usecase.GetCachedBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns the catalog screen state and the active filter (text search + shelf/row finder). Private
 * [MutableStateFlow]s in, read-only [StateFlow]s out (UDF). Offline-first: serve the cached
 * catalog immediately, then refresh from the network and reconcile (ADR 0002).
 */
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getBooks: GetBooksUseCase,
    private val getCachedBooks: GetCachedBooksUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    private val _filter = MutableStateFlow(CatalogFilter())
    val filter: StateFlow<CatalogFilter> = _filter.asStateFlow()

    init {
        load()
    }

    /** Applies a shelf/row finder filter (blank shelf clears that field); keeps any text search. */
    fun applyFinder(shelf: String?, row: Int?) {
        _filter.value = _filter.value.copy(shelf = shelf?.ifBlank { null }, row = row)
        load()
    }

    /** Applies a free-text search (`GET /books?q=`); keeps any active shelf/row finder. */
    fun search(query: String) {
        _filter.value = _filter.value.copy(query = query.ifBlank { null })
        load()
    }

    /** Clears every filter and reloads the full catalog. */
    fun clearFinder() {
        _filter.value = CatalogFilter()
        load()
    }

    fun load() {
        viewModelScope.launch {
            val activeFilter = _filter.value
            // 1. Serve the cache immediately (instant content; stays as a fallback if the net fails).
            val cached = getCachedBooks(activeFilter.shelf, activeFilter.row, activeFilter.query)
            _state.value = if (cached.isEmpty()) CatalogUiState.Loading else CatalogUiState.Content(cached)
            // 2. Refresh from the network and reconcile.
            getBooks(activeFilter.shelf, activeFilter.row, activeFilter.query)
                .onSuccess { books ->
                    _state.value = if (books.isEmpty()) CatalogUiState.Empty else CatalogUiState.Content(books)
                }
                .onFailure { error ->
                    // Offline with a warm cache keeps showing it; otherwise surface the error.
                    if (cached.isEmpty()) {
                        _state.value = CatalogUiState.Error(error.message ?: DEFAULT_ERROR)
                    }
                }
        }
    }

    private companion object {
        const val DEFAULT_ERROR = "Couldn't load the catalog"
    }
}
