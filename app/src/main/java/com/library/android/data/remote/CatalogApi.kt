package com.library.android.data.remote

import com.library.android.data.remote.dto.BookDto
import com.library.android.data.remote.dto.BookListDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit binding for the catalog endpoints in the backend contract. Query/path params match
 * the contract; null query params (shelf/row) are omitted by Retrofit.
 */
interface CatalogApi {
    // One parameter per contract query param (page/size/shelf/row/isbn/q) — a Retrofit binding, not a domain call.
    @Suppress("LongParameterList")
    @GET("books")
    suspend fun listBooks(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = DEFAULT_PAGE_SIZE,
        @Query("shelf") shelf: String? = null,
        @Query("row") row: Int? = null,
        @Query("isbn") isbn: String? = null,
        @Query("q") query: String? = null,
    ): BookListDto

    /** `GET /books/{id}` — 404 surfaces as a Retrofit `HttpException` (handled in the repository). */
    @GET("books/{id}")
    suspend fun getBook(@Path("id") id: String): BookDto

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
