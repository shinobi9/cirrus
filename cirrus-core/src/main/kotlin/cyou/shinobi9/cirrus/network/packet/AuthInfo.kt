package cyou.shinobi9.cirrus.network.packet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthInfo(
    val uid: Int = 0,
    @SerialName("roomid") val roomId: Int,
//    val clientver: String = "1.10.1",
    val platform: String = "web",
    val type: Int = 2,
    @SerialName("protover") val protoVer: Int = 3,
    val key: String? = null,
)
