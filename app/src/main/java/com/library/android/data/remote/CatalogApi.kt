package com.library.android.data.remote

import com.library.android.data.remote.dto.BookListDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit binding for the catalog endpoints defined in the backend contract
 * (`GET /books`). Query params (`page`, `page_size`) match the contract; defaults mirror the
 * contract defaults (page 1, page_size 20).
 */
interface CatalogApi {
    @GET("books")
    suspend fun listBooks(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = DEFAULT_PAGE_SIZE,
    ): BookListDto

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
