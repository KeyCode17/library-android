package com.library.android.data.remote

import com.library.android.data.remote.dto.AssignRoleRequestDto
import com.library.android.data.remote.dto.CreateUserRequestDto
import com.library.android.data.remote.dto.UpdateUserRequestDto
import com.library.android.data.remote.dto.UserListDto
import com.library.android.data.remote.dto.UserSummaryDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Retrofit binding for admin user management (`/users`, all bearer-authed + admin-only server-side). */
interface UserApi {
    @GET("users")
    suspend fun listUsers(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = DEFAULT_PAGE_SIZE,
    ): UserListDto

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequestDto): UserSummaryDto

    @PATCH("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body request: UpdateUserRequestDto): UserSummaryDto

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String)

    @POST("users/{id}/roles")
    suspend fun assignRole(@Path("id") id: String, @Body request: AssignRoleRequestDto): UserSummaryDto

    companion object {
        const val DEFAULT_PAGE_SIZE = 50
    }
}
