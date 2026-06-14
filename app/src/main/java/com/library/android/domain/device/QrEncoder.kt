package com.library.android.domain.device

/** A square QR matrix as plain data (row-major; `true` = a dark module). Drawn by the UI layer. */
class QrMatrix(val size: Int, private val modules: BooleanArray) {
    fun isDark(x: Int, y: Int): Boolean = modules[y * size + x]
}

/**
 * Port for encoding text into a [QrMatrix]. Production adapter uses ZXing; tests use a fake.
 * Returning plain data (not a Bitmap) keeps it pure and JVM-testable.
 */
interface QrEncoder {
    fun encode(content: String): Result<QrMatrix>
}
