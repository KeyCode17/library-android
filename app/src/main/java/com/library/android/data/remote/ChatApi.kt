package com.library.android.data.remote

import com.library.android.data.remote.dto.ChatMessageListDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Retrofit binding for chat history (bearer-authed; live messages go over the WebSocket). */
interface ChatApi {
    @GET("chat/rooms/{room}/messages")
    suspend fun history(
        @Path("room") room: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = DEFAULT_PAGE_SIZE,
    ): ChatMessageListDto

    companion object {
        const val DEFAULT_PAGE_SIZE = 50
    }
}
