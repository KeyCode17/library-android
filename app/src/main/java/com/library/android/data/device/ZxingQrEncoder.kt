package com.library.android.data.device

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.Encoder
import com.library.android.domain.device.QrEncoder
import com.library.android.domain.device.QrMatrix
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production [QrEncoder] using ZXing's low-level encoder to get the compact module grid (not a
 * scaled bitmap), which the UI draws on a Canvas. Pure JVM — could even run in tests, but the
 * gate uses a fake to keep ViewModel/UI tests fast and deterministic.
 */
@Singleton
class ZxingQrEncoder @Inject constructor() : QrEncoder {
    override fun encode(content: String): Result<QrMatrix> = runCatching {
        val code = Encoder.encode(content, ErrorCorrectionLevel.M)
        val byteMatrix = requireNotNull(code.matrix) { "QR encoding produced no matrix" }
        val size = byteMatrix.width
        val modules = BooleanArray(size * size) { index ->
            byteMatrix.get(index % size, index / size).toInt() == 1
        }
        QrMatrix(size, modules)
    }
}
