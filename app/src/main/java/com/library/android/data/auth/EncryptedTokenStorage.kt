package com.library.android.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores the JWT in [EncryptedSharedPreferences] (AES-256, key in the Android Keystore) — never
 * plaintext SharedPreferences. Backs [TokenStorage] for production; unit tests use a fake.
 */
@Singleton
class EncryptedTokenStorage @Inject constructor(
    @ApplicationContext context: Context,
) : TokenStorage {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override fun token(): String? = prefs.getString(KEY_TOKEN, null)

    override fun save(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    override fun clear() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    private companion object {
        const val PREFS_FILE = "auth_secure_prefs"
        const val KEY_TOKEN = "jwt"
    }
}
