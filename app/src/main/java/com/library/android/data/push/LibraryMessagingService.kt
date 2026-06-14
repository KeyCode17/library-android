package com.library.android.data.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.library.android.domain.usecase.RegisterDeviceUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FCM entry point: shows incoming push reminders and re-registers the token on refresh.
 * `@AndroidEntryPoint` + field injection is the only DI option for a framework-instantiated
 * Service (constructor injection isn't possible here).
 */
@AndroidEntryPoint
class LibraryMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var registerDevice: RegisterDeviceUseCase

    @Inject
    lateinit var reminderNotifier: ReminderNotifier

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        val notification = message.notification
        val title = notification?.title ?: "Library reminder"
        val body = notification?.body ?: message.data["message"].orEmpty()
        reminderNotifier.show(title, body)
    }

    override fun onNewToken(token: String) {
        // Re-register the refreshed token (best-effort; 401 if the user isn't signed in).
        scope.launch { registerDevice() }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
