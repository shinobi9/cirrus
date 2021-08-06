package cyou.shinobi9.cirrus.network

import cyou.shinobi9.cirrus.network.packet.Packet
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class JsonResolveException(message: String) : RuntimeException(message)

fun jsonResolveError(message: String): Nothing = throw JsonResolveException("json resolve error : [$message]")

@Serializable
data class Wrapper<T>(
    var data: T?
)

@Suppress("unused")
@Serializable
data class RealRoom(
    @SerialName("room_id") var realRoomId: Int?,
)

@Serializable
data class LoadBalanceInfo(
    val host: String,
    @SerialName("host_server_list") val hostServerList: List<HostServer>,
    @SerialName("max_delay") val maxDelay: Int,
    val port: Int,
    @SerialName("refresh_rate") val refreshRate: Int,
    @SerialName("refresh_row_factor") val refreshRowFactor: Double,
    @SerialName("server_list") val serverList: List<Server>,
    val token: String
)

@Serializable
data class HostServer(
    val host: String,
    val port: Int,
    @SerialName("ws_port") val wsPort: Int,
    @SerialName("wss_port") val wssPort: Int
)

@Serializable
data class Server(
    val host: String,
    val port: Int
)

suspend fun HttpClient.userAvatar(uid: Int): String {
    val response = get<JsonElement>("https://api.bilibili.com/x/space/acc/info") {
        parameter("mid", uid)
    }
    val data = response.jsonObject["data"]?.jsonObject
    val face = data?.get("face")?.jsonPrimitive?.content
    return face ?: jsonResolveError("avatar")
}

suspend fun HttpClient.resolveRealRoomId(roomId: Int): Int {
    val response = get<JsonElement>("https://api.live.bilibili.com/room/v1/Room/room_init") {
        parameter("id", roomId)
    }
    val data = response.jsonObject["data"]?.jsonObject
    val realRoomId = data?.get("room_id")?.jsonPrimitive?.int
    return realRoomId ?: jsonResolveError("real room id")
}

suspend fun HttpClient.loadBalanceWebsocketServer(realRoomId: Int): LoadBalanceInfo {
    val response = get<Wrapper<LoadBalanceInfo>>("https://api.live.bilibili.com/room/v1/Danmu/getConf") {
        parameter("room_id", realRoomId)
        parameter("platform", "pc")
        parameter("player", "web")
    }
    return response.data ?: jsonResolveError("load balance info")
}

suspend inline fun WebSocketSession.sendPacket(packet: Packet) =
    send(packet.toByteBuffer().let { Frame.Binary(true, it) })

class DisconnectException(message: String, throwable: Throwable? = null) : RuntimeException(message, throwable)
