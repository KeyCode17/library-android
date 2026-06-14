package com.library.android.domain.usecase

import com.library.android.domain.model.Book
import com.library.android.domain.repo.CatalogRepository
import javax.inject.Inject

/**
 * Lists the catalog (first page). Single-method use case callable like a function:
 * `getBooks()`.
 */
class GetBooksUseCase @Inject constructor(
    private val repository: CatalogRepository,
) {
    suspend operator fun invoke(): Result<List<Book>> = repository.getBooks()
}
