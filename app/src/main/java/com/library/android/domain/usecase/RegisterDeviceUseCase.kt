package com.library.android.domain.usecase

import com.library.android.domain.push.PushTokenProvider
import com.library.android.domain.repo.NotificationRepository
import javax.inject.Inject

/**
 * Obtains the current push token and registers it with the backend. Best-effort: callers can
 * ignore the result (the app still works without push).
 */
class RegisterDeviceUseCase @Inject constructor(
    private val pushTokenProvider: PushTokenProvider,
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        pushTokenProvider.currentToken().fold(
            onSuccess = { notificationRepository.registerDevice(it) },
            onFailure = { Result.failure(it) },
        )
}
