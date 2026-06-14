package com.library.android.presentation.screens.lending

import com.library.android.MainDispatcherRule
import com.library.android.domain.model.Book
import com.library.android.domain.model.Loan
import com.library.android.domain.model.LoanStatus
import com.library.android.domain.model.Principal
import com.library.android.domain.model.Role
import com.library.android.domain.repo.AuthException
import com.library.android.domain.repo.AuthRepository
import com.library.android.domain.repo.BookLookup
import com.library.android.domain.repo.CatalogRepository
import com.library.android.domain.repo.LoanException
import com.library.android.domain.repo.LoanRepository
import com.library.android.domain.scan.BarcodeScanner
import com.library.android.domain.usecase.BorrowByBarcodeUseCase
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LendingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sampleLoan =
        Loan("l1", "b1", "u1", LoanStatus.BORROWED, "2026-06-01T10:00:00Z", "2026-06-15T10:00:00Z")

    private class FakeLoanRepository : LoanRepository {
        var loans: List<Loan> = emptyList()
        var borrowResult: Result<Loan> = Result.failure(LoanException("unset"))
        var returnResult: Result<Loan> = Result.failure(LoanException("unset"))
        var approveResult: Result<Loan> = Result.failure(LoanException("unset"))
        var lastBorrowedBookId: String? = null

        override suspend fun listLoans(): Result<List<Loan>> = Result.success(loans)
        override suspend fun borrow(bookId: String): Result<Loan> {
            lastBorrowedBookId = bookId
            return borrowResult
        }
        override suspend fun returnLoan(loanId: String): Result<Loan> = returnResult
        override suspend fun approve(loanId: String): Result<Loan> = approveResult
    }

    private class FakeAuthRepository(private val role: Role?) : AuthRepository {
        override suspend fun hasSession(): Boolean = role != null
        override suspend fun login(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun register(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun currentUser(): Result<Principal> =
            role?.let { Result.success(Principal("u1", "u@x.io", it)) }
                ?: Result.failure(AuthException("not authenticated"))
        override suspend fun logout() = Unit
    }

    private class FakeCatalogRepository(private val books: List<Book>) : CatalogRepository {
        override suspend fun getBooks(shelf: String?, row: Int?): Result<List<Book>> =
            Result.success(books)
        override suspend fun getBook(id: String): BookLookup = BookLookup.NotFound
    }

    private class FakeBarcodeScanner(private val result: Result<String>) : BarcodeScanner {
        override suspend fun scan(): Result<String> = result
    }

    private fun viewModel(
        loans: FakeLoanRepository,
        role: Role? = Role.MEMBER,
        catalogBooks: List<Book> = emptyList(),
        scan: Result<String> = Result.failure(LoanException("no scan")),
    ): LendingViewModel = LendingViewModel(
        loanRepository = loans,
        authRepository = FakeAuthRepository(role),
        borrowByBarcode = BorrowByBarcodeUseCase(FakeCatalogRepository(catalogBooks), loans),
        barcodeScanner = FakeBarcodeScanner(scan),
    )

    @Test
    fun `member loads own loans and is not staff`() = runTest(mainDispatcherRule.testDispatcher) {
        val loans = FakeLoanRepository().apply { this.loans = listOf(sampleLoan) }
        val vm = viewModel(loans, role = Role.MEMBER)

        advanceUntilIdle()

        assertEquals(listOf(sampleLoan), vm.state.value.loans)
        assertFalse(vm.state.value.isStaff)
        assertFalse(vm.state.value.isAnonymous)
    }

    @Test
    fun `staff role sets the staff flag`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel(FakeLoanRepository(), role = Role.LIBRARIAN)

        advanceUntilIdle()

        assertTrue(vm.state.value.isStaff)
    }

    @Test
    fun `unauthenticated user is anonymous`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel(FakeLoanRepository(), role = null)

        advanceUntilIdle()

        assertTrue(vm.state.value.isAnonymous)
    }

    @Test
    fun `borrow success borrows the book and messages`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val loans = FakeLoanRepository().apply { borrowResult = Result.success(sampleLoan) }
            val vm = viewModel(loans)
            advanceUntilIdle()

            vm.borrow("b1")
            advanceUntilIdle()

            assertEquals("b1", loans.lastBorrowedBookId)
            assertEquals("Book borrowed", vm.state.value.message)
        }

    @Test
    fun `borrow conflict surfaces the message`() = runTest(mainDispatcherRule.testDispatcher) {
        val loans = FakeLoanRepository().apply {
            borrowResult = Result.failure(LoanException("That book isn't available to borrow"))
        }
        val vm = viewModel(loans)
        advanceUntilIdle()

        vm.borrow("b1")
        advanceUntilIdle()

        assertEquals("That book isn't available to borrow", vm.state.value.message)
    }

    @Test
    fun `return success messages`() = runTest(mainDispatcherRule.testDispatcher) {
        val loans = FakeLoanRepository().apply { returnResult = Result.success(sampleLoan) }
        val vm = viewModel(loans)
        advanceUntilIdle()

        vm.returnLoan("l1")
        advanceUntilIdle()

        assertEquals("Book returned", vm.state.value.message)
    }

    @Test
    fun `scan resolves the isbn and borrows the matching book`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val book = Book("book-x", "Dune", "Herbert", "9780441013593", "R07", 2, true)
            val loans = FakeLoanRepository().apply { borrowResult = Result.success(sampleLoan) }
            val vm = viewModel(
                loans = loans,
                catalogBooks = listOf(book),
                scan = Result.success("9780441013593"),
            )
            advanceUntilIdle()

            vm.scanAndBorrow()
            advanceUntilIdle()

            assertEquals("book-x", loans.lastBorrowedBookId)
            assertEquals("Book borrowed", vm.state.value.message)
        }
}
