package com.library.android.domain.usecase

import com.library.android.domain.model.Loan
import com.library.android.domain.repo.CatalogRepository
import com.library.android.domain.repo.LoanException
import com.library.android.domain.repo.LoanRepository
import javax.inject.Inject

/**
 * Borrows the catalog book matching a scanned barcode (ISBN). The contract has no ISBN lookup,
 * so the book is resolved client-side from the catalog before calling borrow — see the README
 * note about this gap.
 */
class BorrowByBarcodeUseCase @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val loanRepository: LoanRepository,
) {
    suspend operator fun invoke(isbn: String): Result<Loan> =
        catalogRepository.getBooks().fold(
            onSuccess = { books ->
                when (val match = books.firstOrNull { it.isbn == isbn }) {
                    null -> Result.failure(LoanException("That book isn't in the catalog"))
                    else -> loanRepository.borrow(match.id)
                }
            },
            onFailure = { Result.failure(it) },
        )
}
