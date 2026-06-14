package com.library.android.data.remote

import com.library.android.data.remote.dto.DeviceRegistrationDto
import com.library.android.data.remote.dto.PlatformDto
import com.library.android.domain.model.Reminder
import com.library.android.domain.repo.NotificationException
import com.library.android.domain.repo.NotificationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REST-backed [NotificationRepository] (bearer token attached by the interceptor). Maps HTTP
 * failures to user-facing [NotificationException] messages (typed catches only).
 */
@Singleton
class NotificationRemoteRepository @Inject constructor(
    private val api: NotificationApi,
    private val ioDispatcher: CoroutineDispatcher,
) : NotificationRepository {

    override suspend fun registerDevice(token: String): Result<Unit> = withContext(ioDispatcher) {
        guarded({ code -> "Couldn't register this device (code $code)" }) {
            api.registerDevice(DeviceRegistrationDto(token = token, platform = PlatformDto.ANDROID))
        }.map { }
    }

    override suspend fun reminders(): Result<List<Reminder>> = withContext(ioDispatcher) {
        guarded({ code ->
            if (code == HTTP_UNAUTHORIZED) "Sign in to see reminders" else "Couldn't load reminders (code $code)"
        }) { api.list().data.map { it.toDomain() } }
    }

    private inline fun <T> guarded(httpMessage: (Int) -> String, block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: HttpException) {
            Result.failure(NotificationException(httpMessage(e.code()), e))
        } catch (e: IOException) {
            Result.failure(NotificationException("Network error — check your connection", e))
        }

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
    }
}
