package com.library.android.data.auth

/**
 * Secure store for the bearer token. Reads are synchronous so the OkHttp [AuthInterceptor] can
 * attach the token on its own (background) thread.
 */
interface TokenStorage {
    fun token(): String?
    fun save(token: String)
    fun clear()
}
