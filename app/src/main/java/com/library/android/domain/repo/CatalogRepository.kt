package com.library.android.domain.repo

import com.library.android.domain.model.Book

/**
 * Port for catalog reads. The domain depends on this interface; the data layer provides the
 * REST-backed implementation (bound via Hilt).
 */
interface CatalogRepository {
    /**
     * Lists books, optionally narrowed by the book-finder ([shelf]/[row]); both are combinable
     * and optional. `Result` makes success/failure explicit.
     */
    suspend fun getBooks(shelf: String? = null, row: Int? = null): Result<List<Book>>

    /** Fetches a single book; distinguishes 404 (NotFound) from other failures via [BookLookup]. */
    suspend fun getBook(id: String): BookLookup

    /**
     * Resolves a scanned ISBN to a book via the server finder (`GET /books?isbn=`); returns
     * `null` when no book matches. Server-side resolution (not a client-side page scan).
     */
    suspend fun findByIsbn(isbn: String): Result<Book?>
}
