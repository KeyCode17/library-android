package com.library.android.data.remote

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
 * REST-backed [CatalogRepository]. Maps the contract's responses to domain types; runs off the
 * main thread on [ioDispatcher]. Pagination is read but not yet consumed (single-page catalog
 * for 0.1.0; Room cache + paging are an explicit fast-follow).
 */
@Singleton
class CatalogRemoteRepository @Inject constructor(
    private val api: CatalogApi,
    private val ioDispatcher: CoroutineDispatcher,
) : CatalogRepository {

    override suspend fun getBooks(shelf: String?, row: Int?): Result<List<Book>> =
        withContext(ioDispatcher) {
            runCatching { api.listBooks(shelf = shelf, row = row).data.map { it.toDomain() } }
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
