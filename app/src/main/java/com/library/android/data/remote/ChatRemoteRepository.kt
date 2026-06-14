package com.library.android.data.remote

import com.library.android.domain.model.ChatMessage
import com.library.android.domain.repo.ChatException
import com.library.android.domain.repo.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REST-backed [ChatRepository] for room history (bearer token attached by the interceptor).
 */
@Singleton
class ChatRemoteRepository @Inject constructor(
    private val api: ChatApi,
    private val ioDispatcher: CoroutineDispatcher,
) : ChatRepository {

    override suspend fun history(room: String): Result<List<ChatMessage>> =
        withContext(ioDispatcher) {
            try {
                Result.success(api.history(room).data.map { it.toDomain() })
            } catch (e: HttpException) {
                val message = if (e.code() == HTTP_UNAUTHORIZED) {
                    "Sign in to read this room"
                } else {
                    "Couldn't load messages (code ${e.code()})"
                }
                Result.failure(ChatException(message, e))
            } catch (e: IOException) {
                Result.failure(ChatException("Network error — check your connection", e))
            }
        }

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
    }
}
