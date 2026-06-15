package com.library.android.data.remote

import com.library.android.data.local.BookDao
import com.library.android.data.local.toDomain
import com.library.android.data.local.toEntity
import com.library.android.domain.model.Book
import com.library.android.domain.repo.BookLookup
import com.library.android.domain.repo.CatalogRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first [CatalogRepository] (ADR 0002): the network is the source of truth, but the full
 * catalog is mirrored into Room so reads survive offline. A successful **unfiltered** load
 * replaces the cache; filtered loads (shelf/row/text) are served from the server and fall back to
 * the in-memory-filtered cache via [cachedBooks]. Runs off the main thread on [ioDispatcher].
 */
@Singleton
class CatalogRemoteRepository @Inject constructor(
    private val api: CatalogApi,
    private val bookDao: BookDao,
    private val ioDispatcher: CoroutineDispatcher,
) : CatalogRepository {

    override suspend fun getBooks(shelf: String?, row: Int?, query: String?): Result<List<Book>> =
        withContext(ioDispatcher) {
            runCatching {
                val books = api.listBooks(shelf = shelf, row = row, query = query).data.map { it.toDomain() }
                // Only the unfiltered catalog defines the cache, so filtered loads don't evict it.
                if (shelf == null && row == null && query == null) {
                    bookDao.replaceAll(books.map { it.toEntity() })
                }
                books
            }
        }

    override suspend fun cachedBooks(shelf: String?, row: Int?, query: String?): List<Book> =
        withContext(ioDispatcher) {
            bookDao.getAll().map { it.toDomain() }.filter { it.matches(shelf, row, query) }
        }

    override suspend fun findByIsbn(isbn: String): Result<Book?> = withContext(ioDispatcher) {
        runCatching { api.listBooks(isbn = isbn).data.firstOrNull()?.toDomain() }
    }

    override suspend fun getBook(id: String): BookLookup = withContext(ioDispatcher) {
        try {
            BookLookup.Found(api.getBook(id).toDomain())
        } catch (e: HttpException) {
            if (e.code() == HTTP_NOT_FOUND) BookLookup.NotFound else BookLookup.Failed(httpMessage(e))
        } catch (e: IOException) {
            BookLookup.Failed(e.message ?: "Network error")
        }
    }

    private fun httpMessage(e: HttpException): String = "Request failed (${e.code()})"

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}

/** Mirrors the server filter for offline cache reads: shelf/row exact, [query] over title/author/ISBN. */
private fun Book.matches(shelf: String?, row: Int?, query: String?): Boolean {
    val shelfOk = shelf == null || shelf.equals(this.shelf, ignoreCase = true)
    val rowOk = row == null || row == this.row
    val queryOk = query.isNullOrBlank() || query.trim().let { needle ->
        title.contains(needle, ignoreCase = true) ||
            author.contains(needle, ignoreCase = true) ||
            isbn.contains(needle, ignoreCase = true)
    }
    return shelfOk && rowOk && queryOk
}
