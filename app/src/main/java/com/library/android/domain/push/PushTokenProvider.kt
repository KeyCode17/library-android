package com.library.android.domain.push

/**
 * Port for obtaining the device push token. The production adapter reads it from FCM; tests use a
 * fake. Keeping it an interface means device registration is unit-testable without Firebase.
 */
interface PushTokenProvider {
    suspend fun currentToken(): Result<String>
}
