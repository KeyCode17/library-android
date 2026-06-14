package com.library.android.data.push

import com.google.firebase.messaging.FirebaseMessaging
import com.library.android.domain.push.PushTokenProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production [PushTokenProvider] backed by FCM. Requires a real Firebase project +
 * google-services.json at runtime (a deployment requirement — see README); unit tests use a
 * fake, so this isn't exercised in the JVM gate.
 */
@Singleton
class FirebasePushTokenProvider @Inject constructor() : PushTokenProvider {
    override suspend fun currentToken(): Result<String> =
        runCatching { FirebaseMessaging.getInstance().token.await() }
}
