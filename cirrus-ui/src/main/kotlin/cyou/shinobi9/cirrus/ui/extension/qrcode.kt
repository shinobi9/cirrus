package cyou.shinobi9.cirrus.ui.extension

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.ByteArrayOutputStream

const val WIDTH = 150
const val HEIGHT = 150
const val PNG = "png"

fun simpleQrcode(content: String): ByteArray {
    val map = mutableMapOf<EncodeHintType, Any>()
    val byteOutputStream = ByteArrayOutputStream()
    map[EncodeHintType.CHARACTER_SET] = "UTF-8"
    map[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M
    map[EncodeHintType.MARGIN] = 2
    val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, map)
    MatrixToImageWriter.writeToStream(bitMatrix, PNG, byteOutputStream)
    return byteOutputStream.toByteArray()
}
