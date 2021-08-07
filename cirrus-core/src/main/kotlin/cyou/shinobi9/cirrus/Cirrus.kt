package cyou.shinobi9.cirrus

import cyou.shinobi9.cirrus.conf.CirrusConfig
import cyou.shinobi9.cirrus.handler.event.EventHandler
import cyou.shinobi9.cirrus.handler.event.EventType
import cyou.shinobi9.cirrus.handler.event.simpleEventHandler
import cyou.shinobi9.cirrus.handler.message.MessageHandler
import cyou.shinobi9.cirrus.handler.message.rawMessageHandler
import cyou.shinobi9.cirrus.network.DisconnectException
import cyou.shinobi9.cirrus.network.codec.decode
import cyou.shinobi9.cirrus.network.loadBalanceWebsocketServer
import cyou.shinobi9.cirrus.network.packet.Packets
import cyou.shinobi9.cirrus.network.resolveRealRoomId
import cyou.shinobi9.cirrus.network.sendPacket
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import java.net.SocketException
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Suppress("MemberVisibilityCanBePrivate")
class Cirrus(
    var config: CirrusConfig = CirrusConfig(),
    var client: HttpClient = defaultClient,
    var eventHandler: EventHandler? = simpleEventHandler {},
    var messageHandler: MessageHandler? = rawMessageHandler {},
) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = job + useDispatcher + exceptionHandler
    private val cirrusDispatcher by lazy {
        Executors.newFixedThreadPool(config.threadsCount).asCoroutineDispatcher()
    }
    private val useDispatcher = if (config.useDispatchersIO) Dispatchers.IO else cirrusDispatcher
    val job = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LOG.error(throwable) { "exception occur!" }
    }

    private var lastRoomId: Int? = null
    private var lastRealRoomId: Int? = null
    var lastCloseReason: CloseReason? = null
    var session: DefaultWebSocketSession? = null
    var workJob: Job? = null
    fun runningJob() = !job.isCompleted

    /**
     * stop job which may working and shutdown internal thread pool
     */
    fun close() {
        LOG.debug { "closing.." }
        job.cancel()
        if (!config.useDispatchersIO) {
            cirrusDispatcher.close()
        }
    }

    fun stop() {
        LOG.debug { "stop.." }
        workJob?.cancel()
    }

    fun stopAll() {
        LOG.debug { "stop all.." }
        job.cancelChildren()
    }

    @Suppress("LocalVariableName")
    fun connectToBLive(roomId: Int? = null, realRoomId: Int? = null) = launch {
        LOG.debug { "ready to connect" }
        roomId?.let { lastRoomId = it }
        val _realRoomId = realRoomId ?: client.resolveRealRoomId(roomId ?: error("room id can't be null"))
        lastRealRoomId = _realRoomId
        LOG.info { "real room id => $_realRoomId" }
        val loadBalanceInfo = client.loadBalanceWebsocketServer(_realRoomId)
        val urlString = with(loadBalanceInfo) {
            val server = hostServerList.random()
            "wss://${server.host}:${server.wssPort}/sub"
        }
        LOG.info { "use server   => $urlString" }
        workJob = doConnectWithReconnect(_realRoomId, urlString, loadBalanceInfo.token, config.reconnect)
    }

    @OptIn(ExperimentalTime::class)
    private fun doConnectWithReconnect(
        realRoomId: Int,
        urlString: String,
        token: String,
        reconnect: Boolean = true
    ) = launch {
        if (reconnect) {
            while (true) {
                try {
                    doConnect(realRoomId, urlString, token)
                } catch (e: DisconnectException) {
                    LOG.error(e) { "DisconnectException occur!" }
                    ensureActive()
                    LOG.info { "try reconnect..." }
                    delay(Duration.seconds(3))
                    continue
                } catch (e: SocketException) {
                    LOG.error(e) { "SocketException occur!" }
                    ensureActive()
                    LOG.info { "try reconnect..." }
                    delay(Duration.seconds(3))
                    continue
                } catch (e: Exception) {
                    LOG.error(e) { "Exception occur!" }
                }
                break
            }
        } else {
            try {
                doConnect(realRoomId, urlString, token)
            } catch (e: DisconnectException) {
                LOG.error(e) { "DisconnectException occur!" }
            } catch (e: Exception) {
                LOG.error(e) { "exception occur!" }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun doConnect(
        realRoomId: Int,
        urlString: String,
        token: String,
    ) = withContext(coroutineContext) {
        val cirrus = this@Cirrus
        eventHandler?.handle(EventType.CONNECT, cirrus)
        val session = client.webSocketSession { url(urlString) }
        cirrus.session = session

        eventHandler?.handle(EventType.CONNECTED, cirrus)
        LOG.debug { "send auth info" }

        eventHandler?.handle(EventType.LOGIN, cirrus)
        session.sendPacket(Packets.auth(realRoomId, token))

        LOG.debug { "decode message" }

        launch {
            for (message in session.incoming)
                decode(message.buffer, messageHandler, eventHandler)
        }
        launch {
            session.closeReason.await()?.let {
                cirrus.lastCloseReason = it
                with(it) {
                    LOG.info { "code    : $code" }
                    LOG.info { "message : $message" }
                    LOG.info { "reason  : $knownReason" }
                }
                eventHandler?.handle(EventType.DISCONNECT, cirrus)
                throw DisconnectException("disconnect from server")
            }
        }
        // send heart beat every 30s
        launch {
            try {
                while (true) {
                    ensureActive()
                    LOG.debug { "send heart beat packet" }
                    session.sendPacket(Packets.heartBeat)
                    try {
                        delay(Duration.seconds(30))
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
                LOG.error(e) { "exception occur when sending packet!" }
                throw e
            }
        }.join()
    }
}
