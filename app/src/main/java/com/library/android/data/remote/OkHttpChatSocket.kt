package com.library.android.data.remote

import com.library.android.data.auth.TokenStorage
import com.library.android.data.remote.dto.ChatMessageDto
import com.library.android.data.remote.dto.ChatSendDto
import com.library.android.domain.chat.ChatSocket
import com.library.android.domain.chat.ChatSocketEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real [ChatSocket] over OkHttp's WebSocket, connecting to `/ws/chat?room=&token=` with the
 * stored JWT. Exposed as a [Flow] via `callbackFlow`. Exercised by an instrumented test (CI) —
 * NOT the JVM pre-push gate, which uses a fake.
 */
@Singleton
class OkHttpChatSocket @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json,
    private val tokenStorage: TokenStorage,
) : ChatSocket {

    @Volatile
    private var webSocket: WebSocket? = null

    override fun open(room: String): Flow<ChatSocketEvent> = callbackFlow {
        val producer = this
        val token = tokenStorage.token()
        if (token == null) {
            producer.trySend(ChatSocketEvent.Failed("Sign in to join chat"))
        } else {
            val listener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    producer.trySend(ChatSocketEvent.Connected)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    val message = runCatching {
                        json.decodeFromString<ChatMessageDto>(text).toDomain()
                    }.getOrNull()
                    if (message != null) producer.trySend(ChatSocketEvent.Message(message))
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    producer.trySend(ChatSocketEvent.Failed(t.message ?: "Connection failed"))
                    producer.close()
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    producer.trySend(ChatSocketEvent.Closed)
                    producer.close()
                }
            }
            webSocket = client.newWebSocket(buildRequest(room, token), listener)
        }
        awaitClose {
            webSocket?.cancel()
            webSocket = null
        }
    }

    override fun send(body: String) {
        webSocket?.send(json.encodeToString(ChatSendDto(body)))
    }

    override fun close() {
        webSocket?.close(NORMAL_CLOSURE, null)
        webSocket = null
    }

    private fun buildRequest(room: String, token: String): Request {
        val url = WS_URL.toHttpUrl().newBuilder()
            .addQueryParameter("room", room)
            .addQueryParameter("token", token)
            .build()
        return Request.Builder().url(url).build()
    }

    private companion object {
        // Local gateway (matches NetworkModule). OkHttp upgrades the http(s) URL to ws(s).
        const val WS_URL = "http://10.0.2.2:8080/ws/chat"
        const val NORMAL_CLOSURE = 1000
    }
}
