package com.library.android.presentation.screens.lending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.model.Role
import com.library.android.domain.repo.AuthRepository
import com.library.android.domain.repo.LoanRepository
import com.library.android.domain.scan.BarcodeScanner
import com.library.android.domain.usecase.BorrowByBarcodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns the lending state: the user's role (for staff-only Approve), their loans, and borrow/
 * return/approve/scan actions. UDF — private mutable in, read-only out.
 */
@HiltViewModel
class LendingViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val authRepository: AuthRepository,
    private val borrowByBarcode: BorrowByBarcodeUseCase,
    private val barcodeScanner: BarcodeScanner,
) : ViewModel() {

    private val _state = MutableStateFlow(LendingUiState(isLoading = true))
    val state: StateFlow<LendingUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, message = null)
            authRepository.currentUser()
                .onSuccess { principal ->
                    val staff = principal.role == Role.LIBRARIAN || principal.role == Role.ADMIN
                    loanRepository.listLoans()
                        .onSuccess { loans ->
                            _state.value = LendingUiState(loans = loans, isStaff = staff)
                        }
                        .onFailure {
                            _state.value = LendingUiState(isStaff = staff, message = it.message)
                        }
                }
                .onFailure { _state.value = LendingUiState(isAnonymous = true) }
        }
    }

    fun borrow(bookId: String) {
        viewModelScope.launch {
            loanRepository.borrow(bookId)
                .onSuccess { refreshLoans("Book borrowed") }
                .onFailure { _state.value = _state.value.copy(message = it.message) }
        }
    }

    fun returnLoan(loanId: String) {
        viewModelScope.launch {
            loanRepository.returnLoan(loanId)
                .onSuccess { refreshLoans("Book returned") }
                .onFailure { _state.value = _state.value.copy(message = it.message) }
        }
    }

    fun approve(loanId: String) {
        viewModelScope.launch {
            loanRepository.approve(loanId)
                .onSuccess { refreshLoans("Loan approved") }
                .onFailure { _state.value = _state.value.copy(message = it.message) }
        }
    }

    fun scanAndBorrow() {
        viewModelScope.launch {
            barcodeScanner.scan()
                .onSuccess { isbn ->
                    borrowByBarcode(isbn)
                        .onSuccess { refreshLoans("Book borrowed") }
                        .onFailure { _state.value = _state.value.copy(message = it.message) }
                }
                .onFailure { _state.value = _state.value.copy(message = it.message ?: "Scan cancelled") }
        }
    }

    fun messageShown() {
        _state.value = _state.value.copy(message = null)
    }

    private suspend fun refreshLoans(message: String) {
        loanRepository.listLoans()
            .onSuccess { _state.value = _state.value.copy(loans = it, message = message) }
            .onFailure { _state.value = _state.value.copy(message = message) }
    }
}
