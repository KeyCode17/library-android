package com.library.android.data.remote

import com.library.android.data.remote.dto.AuthTokenDto
import com.library.android.data.remote.dto.ChangePasswordRequestDto
import com.library.android.data.remote.dto.CredentialsDto
import com.library.android.data.remote.dto.ForgotPasswordRequestDto
import com.library.android.data.remote.dto.PrincipalDto
import com.library.android.data.remote.dto.ResetPasswordRequestDto
import com.library.android.data.remote.dto.UpdateMeRequestDto
import com.library.android.data.remote.dto.VerifyEmailRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
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

    @PATCH("auth/me")
    suspend fun updateMe(@Body request: UpdateMeRequestDto): PrincipalDto

    @DELETE("auth/me")
    suspend fun deleteMe()

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto)

    // verify/forgot/reset are public — no bearer needed (the emailed token authenticates).
    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequestDto)

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto)

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto)
}
