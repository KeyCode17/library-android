package com.library.android.domain.usecase

import com.library.android.domain.model.Book
import com.library.android.domain.repo.CatalogRepository
import javax.inject.Inject

/**
 * Reads the cached catalog (Room) for instant offline-first display, applying the same
 * [shelf]/[row]/[query] filter. Empty until the first successful network refresh.
 */
class GetCachedBooksUseCase @Inject constructor(
    private val repository: CatalogRepository,
) {
    suspend operator fun invoke(
        shelf: String? = null,
        row: Int? = null,
        query: String? = null,
    ): List<Book> = repository.cachedBooks(shelf, row, query)
}
