package com.library.android.data.device

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi
import com.library.android.domain.device.WifiConfig
import com.library.android.domain.device.WifiProvisioner
import com.library.android.domain.device.WifiProvisionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production [WifiProvisioner] using `WifiNetworkSuggestion` (API 29+). On older devices it
 * degrades gracefully to [WifiProvisionResult.Unsupported]. Exercised by instrumented/manual
 * tests, not the JVM gate.
 */
@Singleton
class AndroidWifiProvisioner @Inject constructor(
    @ApplicationContext private val context: Context,
) : WifiProvisioner {

    override fun isSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    override suspend fun provision(config: WifiConfig): WifiProvisionResult {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return WifiProvisionResult.Unsupported
        return addSuggestion(config)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addSuggestion(config: WifiConfig): WifiProvisionResult {
        val wifiManager = context.getSystemService(WifiManager::class.java)
            ?: return WifiProvisionResult.Failed("WiFi is unavailable on this device")
        val suggestion = WifiNetworkSuggestion.Builder()
            .setSsid(config.ssid)
            .setWpa2Passphrase(config.passphrase)
            .build()
        val status = wifiManager.addNetworkSuggestions(listOf(suggestion))
        return if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            WifiProvisionResult.Success
        } else {
            WifiProvisionResult.Failed("WiFi suggestion rejected (code $status)")
        }
    }
}
