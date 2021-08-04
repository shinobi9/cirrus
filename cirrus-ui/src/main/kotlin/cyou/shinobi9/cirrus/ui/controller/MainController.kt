@file:Suppress("MemberVisibilityCanBePrivate")

package cyou.shinobi9.cirrus.ui.controller

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.handler.message.simpleMessageHandler
import cyou.shinobi9.cirrus.ui.LOG
import cyou.shinobi9.cirrus.ui.model.Danmaku
import cyou.shinobi9.cirrus.ui.model.DanmakuModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.Controller
import kotlin.coroutines.CoroutineContext

class MainController : Controller(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx
    private val backend = Cirrus()

    fun connectToBLive(roomId: Int, danmakuModel: DanmakuModel) {
        backend.messageHandler = simpleMessageHandler {
            onReceiveDanmaku { user, said ->
                addItemToViewModel(user, said, danmakuModel)
            }
        }

        if (backend.runningJob()) {
            backend.stop()
        }

        backend.connectToBLive(roomId)
    }

    private fun addItemToViewModel(user: String, said: String, danmakuModel: DanmakuModel) {
        launch {
            LOG.info { "$user : $said" }
            withContext(Dispatchers.JavaFx) {
                danmakuModel.observableDanmakuList.add(Danmaku(user, said))
            }
        }
    }

    fun stop() {
        backend.stop()
    }
}
