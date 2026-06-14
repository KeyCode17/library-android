package com.library.android.domain.usecase

import com.library.android.domain.model.Book
import com.library.android.domain.repo.CatalogRepository
import javax.inject.Inject

/**
 * Lists the catalog (first page), optionally narrowed by the book-finder ([shelf]/[row]).
 * Callable like a function: `getBooks(shelf, row)`.
 */
class GetBooksUseCase @Inject constructor(
    private val repository: CatalogRepository,
) {
    suspend operator fun invoke(shelf: String? = null, row: Int? = null): Result<List<Book>> =
        repository.getBooks(shelf, row)
}
