package com.library.android.data.remote

import com.library.android.data.remote.dto.BorrowRequestDto
import com.library.android.data.remote.dto.LoanDto
import com.library.android.data.remote.dto.LoanListDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit binding for the lending endpoints (all bearer-authed; the [AuthInterceptor] attaches
 * the token). 4xx responses surface as `HttpException` (mapped in the repository).
 */
interface LoanApi {
    @GET("loans")
    suspend fun listLoans(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = DEFAULT_PAGE_SIZE,
    ): LoanListDto

    @POST("loans")
    suspend fun borrow(@Body request: BorrowRequestDto): LoanDto

    @POST("loans/{id}/return")
    suspend fun returnLoan(@Path("id") id: String): LoanDto

    @POST("loans/{id}/approve")
    suspend fun approve(@Path("id") id: String): LoanDto

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
