package com.library.android.domain.scan

/**
 * Port for scanning a book barcode. The production adapter uses ML Kit + camera; tests use a
 * fake. Keeping it an interface means the borrow-by-scan flow is unit-testable without a camera.
 */
interface BarcodeScanner {
    /** Launches a scan and returns the scanned value (an ISBN), or failure if cancelled/unavailable. */
    suspend fun scan(): Result<String>
}
