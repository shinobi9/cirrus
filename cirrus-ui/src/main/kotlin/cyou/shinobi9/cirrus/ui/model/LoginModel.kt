package cyou.shinobi9.cirrus.ui.model

import cyou.shinobi9.cirrus.ui.extension.simpleQrcode
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import javafx.scene.image.Image
import tornadofx.*
import java.io.ByteArrayInputStream

class LoginModel(val loginInfo: Account = Account()) : ViewModel() {
    var uname: String by loginInfo.unameProp
    var qrcodeUrl: String by loginInfo.qrcodeUrlProp
    var login: Boolean by loginInfo.loginProp

    class Account(
        val qrcodeUrlProp: StringProperty = stringProperty(""),
        val unameProp: StringProperty = stringProperty(""),
        val loginProp: BooleanProperty = booleanProperty(false)
    ) {
        val qrcodeProp = nonNullObjectBinding(qrcodeUrlProp) {
            if (value.isNotEmpty()) Image(ByteArrayInputStream(simpleQrcode(value))) else null
        }
    }
}
