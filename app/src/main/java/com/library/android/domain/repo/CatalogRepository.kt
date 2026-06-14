package com.library.android.domain.repo

import com.library.android.domain.model.Book

/**
 * Port for catalog reads. The domain depends on this interface; the data layer provides the
 * REST-backed implementation (bound via Hilt). `Result` makes success/failure explicit so
 * callers handle errors without try/catch sprawl.
 */
interface CatalogRepository {
    suspend fun getBooks(): Result<List<Book>>
}
