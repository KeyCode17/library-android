package com.library.android.presentation.screens.wifi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.library.android.domain.device.WifiConfig
import com.library.android.domain.device.WifiProvisionResult
import com.library.android.domain.device.WifiProvisioner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Library network. Placeholder credentials — real creds come from config at deployment (README).
internal const val LIBRARY_WIFI_SSID = "Stacks Library"
private const val LIBRARY_WIFI_PASSPHRASE = "stacks-guest-wifi"

/**
 * Provisions the library WiFi via the [WifiProvisioner] port. Starts [WifiUiState.Unsupported]
 * on devices below API 29 (graceful degradation). UDF.
 */
@HiltViewModel
class WifiViewModel @Inject constructor(
    private val wifiProvisioner: WifiProvisioner,
) : ViewModel() {

    private val _state = MutableStateFlow<WifiUiState>(
        if (wifiProvisioner.isSupported()) WifiUiState.Idle else WifiUiState.Unsupported,
    )
    val state: StateFlow<WifiUiState> = _state.asStateFlow()

    fun provision() {
        viewModelScope.launch {
            _state.value = WifiUiState.Provisioning
            _state.value = when (val result = wifiProvisioner.provision(libraryConfig)) {
                WifiProvisionResult.Success -> WifiUiState.Success
                WifiProvisionResult.Unsupported -> WifiUiState.Unsupported
                is WifiProvisionResult.Failed -> WifiUiState.Error(result.reason)
            }
        }
    }

    private val libraryConfig = WifiConfig(LIBRARY_WIFI_SSID, LIBRARY_WIFI_PASSPHRASE)
}
