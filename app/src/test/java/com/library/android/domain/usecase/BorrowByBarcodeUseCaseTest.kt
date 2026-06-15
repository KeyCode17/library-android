package com.library.android.domain.usecase

import com.library.android.domain.model.Book
import com.library.android.domain.model.Loan
import com.library.android.domain.model.LoanStatus
import com.library.android.domain.repo.BookLookup
import com.library.android.domain.repo.CatalogRepository
import com.library.android.domain.repo.LoanException
import com.library.android.domain.repo.LoanRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BorrowByBarcodeUseCaseTest {

    private val book = Book("book-x", "Dune", "Herbert", "9780441013593", "R07", 2, true)
    private val loan = Loan("l1", "book-x", "u1", LoanStatus.BORROWED, "2026-06-01T00:00:00Z", "2026-06-15T00:00:00Z")

    private class FakeCatalogRepository(private val byIsbn: Map<String, Book>) : CatalogRepository {
        var resolvedIsbn: String? = null
        override suspend fun getBooks(shelf: String?, row: Int?, query: String?): Result<List<Book>> =
            Result.success(byIsbn.values.toList())
        override suspend fun cachedBooks(shelf: String?, row: Int?, query: String?): List<Book> =
            byIsbn.values.toList()
        override suspend fun getBook(id: String): BookLookup = BookLookup.NotFound
        override suspend fun findByIsbn(isbn: String): Result<Book?> {
            resolvedIsbn = isbn
            return Result.success(byIsbn[isbn])
        }
    }

    private class FakeLoanRepository(private val borrowResult: Result<Loan>) : LoanRepository {
        var borrowedBookId: String? = null
        override suspend fun listLoans(): Result<List<Loan>> = Result.success(emptyList())
        override suspend fun borrow(bookId: String): Result<Loan> {
            borrowedBookId = bookId
            return borrowResult
        }
        override suspend fun returnLoan(loanId: String): Result<Loan> = borrowResult
        override suspend fun approve(loanId: String): Result<Loan> = borrowResult
    }

    @Test
    fun `resolves the isbn server-side then borrows the matched book`() = runTest {
        val catalog = FakeCatalogRepository(mapOf(book.isbn to book))
        val loans = FakeLoanRepository(Result.success(loan))
        val useCase = BorrowByBarcodeUseCase(catalog, loans)

        val result = useCase(book.isbn)

        assertEquals(book.isbn, catalog.resolvedIsbn) // resolved via GET /books?isbn=
        assertEquals("book-x", loans.borrowedBookId)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `fails without borrowing when no book matches the isbn`() = runTest {
        val catalog = FakeCatalogRepository(emptyMap())
        val loans = FakeLoanRepository(Result.failure(LoanException("unused")))
        val useCase = BorrowByBarcodeUseCase(catalog, loans)

        val result = useCase("0000000000000")

        assertTrue(result.isFailure)
        assertNull(loans.borrowedBookId)
    }
}
