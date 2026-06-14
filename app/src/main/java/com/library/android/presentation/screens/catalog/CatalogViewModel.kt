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
 * Owns the catalog screen state. Private [MutableStateFlow] in, read-only [StateFlow] out
 * (UDF — only the ViewModel mutates state). Loads on construction, like `useQuery` on mount.
 */
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getBooks: GetBooksUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = CatalogUiState.Loading
            getBooks()
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
