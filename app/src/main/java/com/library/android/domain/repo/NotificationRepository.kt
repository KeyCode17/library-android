package com.library.android.domain.repo

import com.library.android.domain.model.Reminder

/** Port for notifications: register this device's push token and read reminder history (bearer-authed). */
interface NotificationRepository {
    /** Registers/refreshes the FCM [token] for the current user (platform = android). */
    suspend fun registerDevice(token: String): Result<Unit>

    /** Reads the user's reminder history. */
    suspend fun reminders(): Result<List<Reminder>>
}
