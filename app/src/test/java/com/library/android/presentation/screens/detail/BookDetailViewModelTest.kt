package com.library.android.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
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
import com.library.android.domain.usecase.GetBookUseCase
import com.library.android.presentation.nav.Routes
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BookDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val book = Book("1", "Dune", "Frank Herbert", "9780441013593", "R07", 2, true)

    private class FakeCatalogRepository(private val lookup: BookLookup) : CatalogRepository {
        override suspend fun getBooks(shelf: String?, row: Int?, query: String?): Result<List<Book>> =
            Result.success(emptyList())
        override suspend fun cachedBooks(shelf: String?, row: Int?, query: String?): List<Book> = emptyList()
        override suspend fun getBook(id: String): BookLookup = lookup
        override suspend fun findByIsbn(isbn: String): Result<Book?> = Result.success(null)
    }

    private class FakeLoanRepository(var borrowResult: Result<Loan>) : LoanRepository {
        var lastBorrowedId: String? = null
        override suspend fun listLoans(): Result<List<Loan>> = Result.success(emptyList())
        override suspend fun borrow(bookId: String): Result<Loan> {
            lastBorrowedId = bookId
            return borrowResult
        }
        override suspend fun returnLoan(loanId: String): Result<Loan> =
            Result.failure(LoanException("unused"))
        override suspend fun approve(loanId: String): Result<Loan> =
            Result.failure(LoanException("unused"))
    }

    private class FakeAuthRepository(private val session: Boolean) : AuthRepository {
        override suspend fun hasSession(): Boolean = session
        override suspend fun login(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun register(email: String, password: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun currentUser(): Result<Principal> = Result.failure(AuthException("unused"))
        override suspend fun logout() = Unit
        override suspend fun updateEmail(email: String): Result<Principal> =
            Result.failure(AuthException("unused"))
        override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> =
            Result.success(Unit)
        override suspend fun deleteAccount(): Result<Unit> = Result.success(Unit)
        override suspend fun forgotPassword(email: String): Result<Unit> = Result.success(Unit)
        override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> =
            Result.success(Unit)
        override suspend fun verifyEmail(token: String): Result<Unit> = Result.success(Unit)
    }

    private val loan = Loan("L1", "1", "U1", LoanStatus.BORROWED, "2026-06-15", "2026-06-29")

    private fun viewModel(
        lookup: BookLookup = BookLookup.Found(book),
        session: Boolean = true,
        borrowResult: Result<Loan> = Result.success(loan),
        loans: FakeLoanRepository = FakeLoanRepository(borrowResult),
        id: String = "1",
    ): BookDetailViewModel =
        BookDetailViewModel(
            getBook = GetBookUseCase(FakeCatalogRepository(lookup)),
            loanRepository = loans,
            authRepository = FakeAuthRepository(session),
            savedStateHandle = SavedStateHandle(mapOf(Routes.ARG_BOOK_ID to id)),
        )

    @Test
    fun `starts loading then emits content`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel()

        assertEquals(BookDetailUiState.Loading, viewModel.state.value)

        advanceUntilIdle()

        assertEquals(BookDetailUiState.Content(book), viewModel.state.value)
    }

    @Test
    fun `maps not found to NotFound`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(lookup = BookLookup.NotFound)

        advanceUntilIdle()

        assertEquals(BookDetailUiState.NotFound, viewModel.state.value)
    }

    @Test
    fun `maps failure to Error with message`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(lookup = BookLookup.Failed("Request failed (500)"))

        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("expected Error but was $state", state is BookDetailUiState.Error)
        assertEquals("Request failed (500)", (state as BookDetailUiState.Error).message)
    }

    @Test
    fun `borrow success marks the book unavailable and confirms`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val loans = FakeLoanRepository(Result.success(loan))
            val viewModel = viewModel(loans = loans)
            advanceUntilIdle()

            viewModel.borrow()
            advanceUntilIdle()

            assertEquals("1", loans.lastBorrowedId)
            assertFalse((viewModel.state.value as BookDetailUiState.Content).book.available)
            assertEquals("Borrowed — find it on the shelf", viewModel.borrow.value.message)
        }

    @Test
    fun `borrow while anonymous requests login without calling the network`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val loans = FakeLoanRepository(Result.success(loan))
            val viewModel = viewModel(session = false, loans = loans)
            advanceUntilIdle()

            viewModel.borrow()
            advanceUntilIdle()

            assertTrue(viewModel.borrow.value.requireLogin)
            assertEquals(null, loans.lastBorrowedId)
        }

    @Test
    fun `borrow conflict surfaces the already-on-loan message`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = viewModel(
                borrowResult = Result.failure(LoanException("That book isn't available to borrow")),
            )
            advanceUntilIdle()

            viewModel.borrow()
            advanceUntilIdle()

            assertEquals("That book isn't available to borrow", viewModel.borrow.value.message)
        }
}
