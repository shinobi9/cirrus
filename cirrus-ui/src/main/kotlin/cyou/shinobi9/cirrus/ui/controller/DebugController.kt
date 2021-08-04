@file:Suppress("MemberVisibilityCanBePrivate")

package cyou.shinobi9.cirrus.ui.controller

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.handler.message.simpleMessageHandler
import cyou.shinobi9.cirrus.ui.LOG
import cyou.shinobi9.cirrus.ui.model.DebugDanmaku
import cyou.shinobi9.cirrus.ui.model.DebugDanmakuModel
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.Controller
import kotlin.coroutines.CoroutineContext

class DebugController : Controller(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx
    private val backend = Cirrus()

    fun connectToBLive(roomId: Int, danmakuModel: DebugDanmakuModel) {
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

    private fun addItemToViewModel(user: String, said: String, danmakuModel: DebugDanmakuModel) {
        launch {
            LOG.info { "$user : $said" }
            withContext(currentCoroutineContext()) {
                danmakuModel.observableDebugDanmakuList.add(DebugDanmaku(user, said))
            }
        }
    }

    fun stop() {
        backend.stop()
    }
}
