package com.library.android.data.scan

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.library.android.domain.scan.BarcodeScanner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ML Kit adapter for [BarcodeScanner] using Google's bundled code-scanner UI (which handles the
 * camera + permission prompt). Integration-tested on a device/CI — not exercised in the JVM
 * pre-push gate (unit tests use a fake scanner).
 */
@Singleton
class MlKitBarcodeScanner @Inject constructor(
    @ApplicationContext private val context: Context,
) : BarcodeScanner {

    override suspend fun scan(): Result<String> = runCatching {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8)
            .build()
        val barcode = GmsBarcodeScanning.getClient(context, options).startScan().await()
        requireNotNull(barcode.rawValue) { "Scanned barcode had no value" }
    }
}
