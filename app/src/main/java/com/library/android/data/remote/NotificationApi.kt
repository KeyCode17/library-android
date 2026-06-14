package com.library.android.data.remote

import com.library.android.data.remote.dto.DeviceDto
import com.library.android.data.remote.dto.DeviceRegistrationDto
import com.library.android.data.remote.dto.NotificationListDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** Retrofit binding for notifications (bearer-authed; token attached by the interceptor). */
interface NotificationApi {
    @POST("notifications/devices")
    suspend fun registerDevice(@Body registration: DeviceRegistrationDto): DeviceDto

    @GET("notifications")
    suspend fun list(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = DEFAULT_PAGE_SIZE,
    ): NotificationListDto

    companion object {
        const val DEFAULT_PAGE_SIZE = 50
    }
}
