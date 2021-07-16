package cyou.shinobi9.cirrus.ui.controller

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.conf.CirrusConfig
import cyou.shinobi9.cirrus.handler.message.simpleMessageHandler
import cyou.shinobi9.cirrus.ui.LOG
import cyou.shinobi9.cirrus.ui.model.Danmaku
import cyou.shinobi9.cirrus.ui.model.DanmakuModel
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class MainController : Controller(), CoroutineScope {
    private val job: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    lateinit var danmakuListener: Job

    fun connectToBLive(roomId: Int, danmakuModel: DanmakuModel) {
        if (::danmakuListener.isInitialized)
            danmakuListener.cancel()
        val backend = Cirrus(
            config = CirrusConfig(
                messageHandler =
                simpleMessageHandler {
                    onReceiveDanmaku { user, said ->
                        runBlocking {
                            withContext(Dispatchers.JavaFx) {
                                LOG.info { "$user : $said" }
                                danmakuModel.observableDanmakuList.add(Danmaku(user, said))
                            }
                        }
                    }
                }
            )
        )
        danmakuListener = backend.connectToBLive(roomId)
    }
}
