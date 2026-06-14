package com.library.android.domain.usecase

import com.library.android.domain.model.Reminder
import com.library.android.domain.push.PushTokenProvider
import com.library.android.domain.repo.NotificationException
import com.library.android.domain.repo.NotificationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterDeviceUseCaseTest {

    private class FakePushTokenProvider(private val result: Result<String>) : PushTokenProvider {
        override suspend fun currentToken(): Result<String> = result
    }

    private class FakeNotificationRepository : NotificationRepository {
        var registeredToken: String? = null
        override suspend fun registerDevice(token: String): Result<Unit> {
            registeredToken = token
            return Result.success(Unit)
        }
        override suspend fun reminders(): Result<List<Reminder>> = Result.success(emptyList())
    }

    @Test
    fun `posts the token when available`() = runTest {
        val repo = FakeNotificationRepository()
        val useCase = RegisterDeviceUseCase(FakePushTokenProvider(Result.success("fcm-token")), repo)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals("fcm-token", repo.registeredToken)
    }

    @Test
    fun `does not post when no token is available`() = runTest {
        val repo = FakeNotificationRepository()
        val useCase = RegisterDeviceUseCase(
            FakePushTokenProvider(Result.failure(NotificationException("no token"))),
            repo,
        )

        val result = useCase()

        assertTrue(result.isFailure)
        assertNull(repo.registeredToken)
    }
}
