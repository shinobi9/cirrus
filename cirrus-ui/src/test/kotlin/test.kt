import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.EncodeHintType.*
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File

const val WIDTH = 150
const val HEIGHT = 150

fun main() {
    val map = mutableMapOf<EncodeHintType, Any>()
    map[CHARACTER_SET] = "UTF-8"
    map[ERROR_CORRECTION] = ErrorCorrectionLevel.M
    map[MARGIN] = 2
    val bitMatrix = MultiFormatWriter().encode("hello world 你好世界", BarcodeFormat.QR_CODE, WIDTH, HEIGHT, map)
    MatrixToImageWriter.writeToPath(bitMatrix, "png", File("${System.getProperty("user.dir")}/test.png").toPath())
}
