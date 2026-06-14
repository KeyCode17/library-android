package com.library.android.data.remote

import com.library.android.domain.model.Book
import com.library.android.domain.repo.CatalogRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REST-backed [CatalogRepository]. Calls the contract's `GET /books`, maps the page's `data`
 * to domain [Book]s, and wraps the call in `runCatching` so failures surface as
 * `Result.failure` rather than thrown exceptions. Pagination is read but not yet consumed
 * (single-page catalog for 0.1.0; Room cache + paging are an explicit fast-follow).
 */
@Singleton
class CatalogRemoteRepository @Inject constructor(
    private val api: CatalogApi,
    private val ioDispatcher: CoroutineDispatcher,
) : CatalogRepository {

    override suspend fun getBooks(): Result<List<Book>> = withContext(ioDispatcher) {
        runCatching { api.listBooks().data.map { it.toDomain() } }
    }
}
