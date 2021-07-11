package cyou.shinobi9.cirrus.network

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.LOG
import cyou.shinobi9.cirrus.network.codec.decode
import cyou.shinobi9.cirrus.network.packet.Packet
import cyou.shinobi9.cirrus.network.packet.Packets
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class JsonResolveException(message: String) : RuntimeException(message)

fun jsonResolveError(message: String = "json resolve error"): Nothing = throw JsonResolveException(message)

@Serializable
data class Wrapper<T>(
    var data: T?
)

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

@OptIn(ExperimentalTime::class)
suspend fun HttpClient.connectToBilibiliLive(
    realRoomId: Int,
    urlString: String,
    token: String,
    cirrus: Cirrus
) {
    wss(urlString = urlString) {
        withContext(cirrus.coroutineContext) {
            try {
                LOG.debug { "send auth info" }
                sendPacket(Packets.auth(realRoomId, token))
                delay(200)
                LOG.debug { "decode message" }
                launch {
                    for (message in incoming)
                        decode(message.buffer, cirrus.messageHandler, cirrus.eventHandler)
                }
                launch {
                    closeReason.await()?.let {
                        with(it) {
                            LOG.info { "code    : $code" }
                            LOG.info { "message : $message" }
                            LOG.info { "reason  : $knownReason" }
                        }
                    }
                }
                // send heart beat every 30s
                while (true) {
                    LOG.debug { "send heart beat packet" }
                    sendPacket(Packets.heartBeat)
                    delay(Duration.seconds(30))
                }
            } catch (e: Exception) {
                LOG.error(e) { "exception occur in connecting!" }
            }
        }
    }
}
