package cyou.shinobi9.cirrus

import cyou.shinobi9.cirrus.conf.CirrusConfig
import cyou.shinobi9.cirrus.handler.event.EventHandler
import cyou.shinobi9.cirrus.handler.event.simpleEventHandler
import cyou.shinobi9.cirrus.handler.message.MessageHandler
import cyou.shinobi9.cirrus.handler.message.rawMessageHandler
import cyou.shinobi9.cirrus.network.connectToBilibiliLive
import cyou.shinobi9.cirrus.network.loadBalanceWebsocketServer
import cyou.shinobi9.cirrus.network.resolveRealRoomId
import io.ktor.client.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class Cirrus(
    private val config: CirrusConfig = CirrusConfig(),
    val client: HttpClient = defaultClient,
    val eventHandler: EventHandler? = simpleEventHandler {},
    val messageHandler: MessageHandler? = rawMessageHandler {},
) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = job + useDispatcher + exceptionHandler
    private val cirrusDispatcher by lazy {
        Executors.newFixedThreadPool(config.threadsCount).asCoroutineDispatcher()
    }
    private val useDispatcher = if (config.useDispatchersIO) Dispatchers.IO else cirrusDispatcher
    private val job = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LOG.error(throwable) { "exception occur!" }
    }

    private var lastRoomId: Int? = null
    private var lastRealRoomId: Int? = null
    var lastCloseReason: CloseReason? = null

    /**
     * stop job which may working and shutdown internal thread pool
     */
    fun close() {
        job.cancel()
        if (!config.useDispatchersIO) {
            cirrusDispatcher.close()
        }
    }

    @Suppress("LocalVariableName")
    fun connectToBLive(roomId: Int? = null, realRoomId: Int? = null) = launch {
        roomId?.let { lastRoomId = it }
        val _realRoomId = realRoomId ?: client.resolveRealRoomId(roomId ?: error("room id can't be null"))
        lastRealRoomId = _realRoomId
        LOG.info { "real room id => $_realRoomId" }
        val loadBalanceInfo = client.loadBalanceWebsocketServer(_realRoomId)
        val urlString = with(loadBalanceInfo) {
            val server = hostServerList[Random.nextInt(3)]
            "wss://${server.host}:${server.wssPort}/sub"
        }
        LOG.info { "use server   => $urlString" }
        connectToBilibiliLive(_realRoomId, urlString, loadBalanceInfo.token, job)
    }
}
