package cyou.shinobi9.cirrus.ui.controller

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.handler.message.MessageHandler
import cyou.shinobi9.cirrus.handler.message.rawMessageHandler
import cyou.shinobi9.cirrus.ui.extension.handleMessage
import cyou.shinobi9.cirrus.ui.model.DanmakuListModel
import cyou.shinobi9.cirrus.ui.model.RoomModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.Controller
import kotlin.coroutines.CoroutineContext

class MainController : Controller(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx
    private val backend = Cirrus()

    fun connect(danmakuListModel: DanmakuListModel, roomModel: RoomModel) {
        backend.messageHandler = configureHandler(danmakuListModel)
        if (backend.runningJob()) {
            backend.stopAll()
        }
        backend.connectToBLive(roomModel.roomId)
    }

    private fun configureHandler(danmakuListModel: DanmakuListModel): MessageHandler {
        val container = danmakuListModel.observableDanmakuList
        return rawMessageHandler {
            onMessage {
                handleMessage(it, container)
            }
        }
    }
}
