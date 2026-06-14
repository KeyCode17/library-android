package com.library.android.domain.device

/** The library WiFi network to provision. */
data class WifiConfig(
    val ssid: String,
    val passphrase: String,
)

/** Outcome of a provisioning attempt. */
sealed interface WifiProvisionResult {
    data object Success : WifiProvisionResult
    data object Unsupported : WifiProvisionResult
    data class Failed(val reason: String) : WifiProvisionResult
}

/**
 * Port for provisioning WiFi via platform APIs (WifiNetworkSuggestion, API 29+). Production
 * adapter wraps WifiManager; tests use a fake. [isSupported] enables graceful degradation on
 * older API levels.
 */
interface WifiProvisioner {
    fun isSupported(): Boolean
    suspend fun provision(config: WifiConfig): WifiProvisionResult
}
