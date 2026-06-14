package com.library.android.data.remote

import com.library.android.data.remote.dto.AuthTokenDto
import com.library.android.data.remote.dto.CredentialsDto
import com.library.android.data.remote.dto.PrincipalDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit binding for the IAM endpoints. `/auth/me` relies on the [AuthInterceptor] to attach
 * the bearer token. 4xx responses surface as Retrofit `HttpException` (mapped in the repository).
 */
interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body credentials: CredentialsDto): PrincipalDto

    @POST("auth/login")
    suspend fun login(@Body credentials: CredentialsDto): AuthTokenDto

    @GET("auth/me")
    suspend fun me(): PrincipalDto
}
