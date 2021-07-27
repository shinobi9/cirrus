package cyou.shinobi9.cirrus.network.packet

import cyou.shinobi9.cirrus.json
import kotlinx.serialization.encodeToString
import java.nio.ByteBuffer

object Packets {
    private const val heartBeatContent = "[object Object]"
    val heartBeat = Packet.createPacket(
        PacketMask(
            Version.WS_BODY_PROTOCOL_VERSION_INT,
            Operation.HEARTBEAT
        ),
        ByteBuffer.wrap(heartBeatContent.toByteArray())
    )
    val auth = fun(roomId: Int, token: String) = Packet.createPacket(
        PacketMask(
            Version.WS_BODY_PROTOCOL_VERSION_INT,
            Operation.AUTH
        ),
        ByteBuffer.wrap(
            json.encodeToString(AuthInfo(roomId = roomId, key = token)).toByteArray()
        )
    )
}
