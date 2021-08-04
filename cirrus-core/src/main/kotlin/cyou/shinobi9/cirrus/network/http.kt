package cyou.shinobi9.cirrus.network

import cyou.shinobi9.cirrus.network.packet.Packet
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

class JsonResolveException(message: String) : RuntimeException(message)

fun jsonResolveError(message: String = "json resolve error"): Nothing = throw JsonResolveException(message)

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

suspend fun HttpClient.resolveRealRoomId(roomId: Int): Int {
    val response = get<JsonElement>("https://api.live.bilibili.com/room/v1/Room/room_init") {
        parameter("id", roomId)
    }
    val data = response.jsonObject["data"]
    val realRoomId = (data as? JsonObject)?.get("room_id")?.jsonPrimitive?.int
    return realRoomId ?: jsonResolveError("resolve real room id error")
}

suspend fun HttpClient.loadBalanceWebsocketServer(realRoomId: Int): LoadBalanceInfo {
    val response = get<Wrapper<LoadBalanceInfo>>("https://api.live.bilibili.com/room/v1/Danmu/getConf") {
        parameter("room_id", realRoomId)
        parameter("platform", "pc")
        parameter("player", "web")
    }
    return response.data ?: jsonResolveError("resolve load balance info error")
}

suspend inline fun WebSocketSession.sendPacket(packet: Packet) =
    send(packet.toByteBuffer().let { Frame.Binary(true, it) })

class DisconnectException(message: String, throwable: Throwable? = null) : RuntimeException(message, throwable)
