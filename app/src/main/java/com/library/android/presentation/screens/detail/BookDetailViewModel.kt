package com.library.android.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.repo.AuthRepository
import com.library.android.domain.repo.BookLookup
import com.library.android.domain.repo.LoanRepository
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
 * Also drives the detail Borrow action ([POST /loans]) via a separate [borrow] state.
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBook: GetBookUseCase,
    private val loanRepository: LoanRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle[Routes.ARG_BOOK_ID]) {
        "BookDetail requires a '${Routes.ARG_BOOK_ID}' navigation argument"
    }

    private val _state = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val state: StateFlow<BookDetailUiState> = _state.asStateFlow()

    private val _borrow = MutableStateFlow(BorrowUiState())
    val borrow: StateFlow<BorrowUiState> = _borrow.asStateFlow()

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

    /** Borrows the shown book. Anonymous users are routed to sign-in; 409 surfaces "on loan". */
    fun borrow() {
        val content = _state.value as? BookDetailUiState.Content ?: return
        viewModelScope.launch {
            _borrow.value = BorrowUiState(inProgress = true)
            if (!authRepository.hasSession()) {
                _borrow.value = BorrowUiState(requireLogin = true)
                return@launch
            }
            loanRepository.borrow(content.book.id)
                .onSuccess {
                    _state.value = BookDetailUiState.Content(content.book.copy(available = false))
                    _borrow.value = BorrowUiState(message = "Borrowed — find it on the shelf")
                }
                .onFailure { _borrow.value = BorrowUiState(message = it.message ?: "Couldn't borrow") }
        }
    }

    /** Clears the transient borrow message after the screen has shown it. */
    fun messageShown() {
        _borrow.value = _borrow.value.copy(message = null)
    }

    /** Clears the one-shot login request after the screen has navigated. */
    fun loginHandled() {
        _borrow.value = _borrow.value.copy(requireLogin = false)
    }
}
