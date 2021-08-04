package cyou.shinobi9.cirrus.ui.controller

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.handler.message.SimpleMessageHandlerImpl
import cyou.shinobi9.cirrus.handler.message.simpleMessageHandler
import cyou.shinobi9.cirrus.ui.model.Danmaku
import cyou.shinobi9.cirrus.ui.model.DanmakuModel
import javafx.collections.ObservableList
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class MainController : Controller(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx
    private val backend = Cirrus()

    fun connect(danmakuModel: DanmakuModel) {
        backend.messageHandler = configureHandler(danmakuModel)
        launch {
            backend.connectToBLive(92613)
        }
    }

    private fun configureHandler(danmakuModel: DanmakuModel): SimpleMessageHandlerImpl {
        val container = danmakuModel.observableDanmakuList
        return simpleMessageHandler {
            onReceiveDanmaku { user, said ->
                handle(container, user, said)
            }
            onReceiveGift { user, num, giftName ->
                handle(container, user, "$user 送出了 $num 个 $giftName")
            }
            onUserEnterInLiveRoom {
                handle(container, it, "$it 进入了直播间")
            }
        }
    }

    private fun handle(container: ObservableList<Danmaku>, user: String, content: String, id: Int? = null) {
        launch {
            withContext(currentCoroutineContext()) {
                container.queueAdd(Danmaku(user, content))
            }
        }
    }

    fun ObservableList<Danmaku>.queueAdd(danmaku: Danmaku) {
        if (size > 10) {
            removeAt(0)
        }
        add(danmaku)
    }
}
