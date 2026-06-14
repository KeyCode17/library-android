package com.library.android.domain.usecase

import com.library.android.domain.model.Loan
import com.library.android.domain.repo.CatalogRepository
import com.library.android.domain.repo.LoanException
import com.library.android.domain.repo.LoanRepository
import javax.inject.Inject

/**
 * Borrows the book matching a scanned barcode (ISBN). Resolves the ISBN to a book **server-side**
 * via `GET /books?isbn=` (the catalog finder), then borrows it — no client-side page scanning.
 */
class BorrowByBarcodeUseCase @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val loanRepository: LoanRepository,
) {
    suspend operator fun invoke(isbn: String): Result<Loan> =
        catalogRepository.findByIsbn(isbn).fold(
            onSuccess = { book ->
                if (book == null) {
                    Result.failure(LoanException("That book isn't in the catalog"))
                } else {
                    loanRepository.borrow(book.id)
                }
            },
            onFailure = { Result.failure(it) },
        )
}
