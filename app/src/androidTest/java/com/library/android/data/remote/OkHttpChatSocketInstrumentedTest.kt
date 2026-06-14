package com.library.android.data.remote

import com.library.android.data.auth.TokenStorage
import com.library.android.domain.chat.ChatSocketEvent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

/**
 * Instrumented test for the REAL OkHttp WebSocket adapter (runs on a device/emulator, CI-only —
 * not the JVM pre-push gate). A full round-trip needs a live gateway; here we exercise the real
 * adapter's no-token path, which must emit [ChatSocketEvent.Failed] without a network call.
 */
@RunWith(AndroidJUnit4::class)
class OkHttpChatSocketInstrumentedTest {

    private class NoTokenStorage : TokenStorage {
        override fun token(): String? = null
        override fun save(token: String) = Unit
        override fun clear() = Unit
    }

    @Test
    fun openWithoutTokenEmitsFailed() = runBlocking {
        val socket = OkHttpChatSocket(
            client = OkHttpClient(),
            json = Json { ignoreUnknownKeys = true },
            tokenStorage = NoTokenStorage(),
        )

        val event = withTimeout(TIMEOUT_MS) { socket.open("ask-a-librarian").first() }

        assertTrue("expected Failed but was $event", event is ChatSocketEvent.Failed)
    }

    private companion object {
        const val TIMEOUT_MS = 2_000L
    }
}
