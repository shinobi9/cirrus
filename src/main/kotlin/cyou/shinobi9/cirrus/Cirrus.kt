package cyou.shinobi9.cirrus

import cyou.shinobi9.cirrus.conf.CirrusConfig
import cyou.shinobi9.cirrus.handler.event.EventHandler
import cyou.shinobi9.cirrus.handler.message.MessageHandler
import cyou.shinobi9.cirrus.network.connectToBilibiliLive
import cyou.shinobi9.cirrus.network.loadBalanceWebsocketServer
import cyou.shinobi9.cirrus.network.resolveRealRoomId
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

val LOG = KotlinLogging.logger { }

class Cirrus(private val config: CirrusConfig = CirrusConfig()) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = job + useDispatcher + exceptionHandler
    private val cirrusDispatcher by lazy {
        Executors.newFixedThreadPool(config.threadsCount).asCoroutineDispatcher()
    }
    private val useDispatcher = if (config.useDispatchersIO) Dispatchers.IO else cirrusDispatcher
    private val client = config.httpClient
    private val job = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LOG.error(throwable) { "exception occur!" }
    }
    val eventHandler: EventHandler = config.eventHandler
    val messageHandler: MessageHandler = config.messageHandler

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
        val _realRoomId = realRoomId ?: client.resolveRealRoomId(roomId ?: error("room id can't be null"))
        LOG.info { "real room id => $_realRoomId" }
        val loadBalanceInfo = client.loadBalanceWebsocketServer(_realRoomId)
        val urlString = with(loadBalanceInfo) {
            val server = hostServerList[Random.nextInt(3)]
            "wss://${server.host}:${server.wssPort}/sub"
        }
        LOG.info { "use          => $urlString" }
        client.connectToBilibiliLive(_realRoomId, urlString, loadBalanceInfo.token, this@Cirrus)
    }
}
