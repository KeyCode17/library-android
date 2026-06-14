package com.library.android.domain.usecase

import com.library.android.domain.repo.BookLookup
import com.library.android.domain.repo.CatalogRepository
import javax.inject.Inject

/** Fetches one book by id. Returns [BookLookup] so callers can branch on Found/NotFound/Failed. */
class GetBookUseCase @Inject constructor(
    private val repository: CatalogRepository,
) {
    suspend operator fun invoke(id: String): BookLookup = repository.getBook(id)
}
