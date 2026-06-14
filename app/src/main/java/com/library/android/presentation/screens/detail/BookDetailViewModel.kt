package com.library.android.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.repo.BookLookup
import com.library.android.domain.usecase.GetBookUseCase
import com.library.android.presentation.nav.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns the book-detail state. Reads the book id from the navigation arguments
 * ([SavedStateHandle]); maps the repository's [BookLookup] to the UiState (404 -> NotFound).
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBook: GetBookUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle[Routes.ARG_BOOK_ID]) {
        "BookDetail requires a '${Routes.ARG_BOOK_ID}' navigation argument"
    }

    private val _state = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val state: StateFlow<BookDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = BookDetailUiState.Loading
            _state.value = when (val lookup = getBook(bookId)) {
                is BookLookup.Found -> BookDetailUiState.Content(lookup.book)
                BookLookup.NotFound -> BookDetailUiState.NotFound
                is BookLookup.Failed -> BookDetailUiState.Error(lookup.message)
            }
        }
    }
}
