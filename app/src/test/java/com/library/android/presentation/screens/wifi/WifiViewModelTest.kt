package com.library.android.presentation.screens.wifi

import com.library.android.MainDispatcherRule
import com.library.android.domain.device.WifiConfig
import com.library.android.domain.device.WifiProvisionResult
import com.library.android.domain.device.WifiProvisioner
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class WifiViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeWifiProvisioner(
        private val supported: Boolean,
        private val result: WifiProvisionResult,
    ) : WifiProvisioner {
        var provisionedConfig: WifiConfig? = null
        override fun isSupported(): Boolean = supported
        override suspend fun provision(config: WifiConfig): WifiProvisionResult {
            provisionedConfig = config
            return result
        }
    }

    @Test
    fun `starts idle when supported`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = WifiViewModel(FakeWifiProvisioner(supported = true, result = WifiProvisionResult.Success))
        assertEquals(WifiUiState.Idle, vm.state.value)
    }

    @Test
    fun `starts unsupported below api 29`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = WifiViewModel(FakeWifiProvisioner(supported = false, result = WifiProvisionResult.Unsupported))
        assertEquals(WifiUiState.Unsupported, vm.state.value)
    }

    @Test
    fun `provision success emits success and sends the library config`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fake = FakeWifiProvisioner(supported = true, result = WifiProvisionResult.Success)
            val vm = WifiViewModel(fake)

            vm.provision()
            advanceUntilIdle()

            assertEquals(WifiUiState.Success, vm.state.value)
            assertEquals(LIBRARY_WIFI_SSID, fake.provisionedConfig?.ssid)
        }

    @Test
    fun `provision failure emits error`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = WifiViewModel(
            FakeWifiProvisioner(supported = true, result = WifiProvisionResult.Failed("rejected")),
        )

        vm.provision()
        advanceUntilIdle()

        assertEquals(WifiUiState.Error("rejected"), vm.state.value)
    }
}
