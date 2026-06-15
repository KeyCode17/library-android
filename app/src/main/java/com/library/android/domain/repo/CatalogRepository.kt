package com.library.android.domain.repo

import com.library.android.domain.model.Book

/**
 * Port for catalog reads. The domain depends on this interface; the data layer provides the
 * REST-backed implementation (bound via Hilt).
 */
interface CatalogRepository {
    /**
     * Lists books, optionally narrowed by the book-finder ([shelf]/[row]) and free-text [query]
     * (`GET /books?q=`); all combinable and optional. Refreshes the offline cache (ADR 0002) on a
     * successful unfiltered load. `Result` makes success/failure explicit.
     */
    suspend fun getBooks(
        shelf: String? = null,
        row: Int? = null,
        query: String? = null,
    ): Result<List<Book>>

    /**
     * Reads the cached catalog (Room) for instant display, applying the same [shelf]/[row]/[query]
     * filter in-memory. Returns an empty list when nothing is cached yet.
     */
    suspend fun cachedBooks(
        shelf: String? = null,
        row: Int? = null,
        query: String? = null,
    ): List<Book>

    /** Fetches a single book; distinguishes 404 (NotFound) from other failures via [BookLookup]. */
    suspend fun getBook(id: String): BookLookup

    /**
     * Resolves a scanned ISBN to a book via the server finder (`GET /books?isbn=`); returns
     * `null` when no book matches. Server-side resolution (not a client-side page scan).
     */
    suspend fun findByIsbn(isbn: String): Result<Book?>
}
