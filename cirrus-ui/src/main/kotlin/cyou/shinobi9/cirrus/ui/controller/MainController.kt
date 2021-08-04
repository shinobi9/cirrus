package cyou.shinobi9.cirrus.ui.controller

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.handler.message.MessageHandler
import cyou.shinobi9.cirrus.handler.message.rawMessageHandler
import cyou.shinobi9.cirrus.network.packet.CMD
import cyou.shinobi9.cirrus.network.packet.searchCMD
import cyou.shinobi9.cirrus.ui.LOG
import cyou.shinobi9.cirrus.ui.model.Danmaku
import cyou.shinobi9.cirrus.ui.model.DanmakuModel
import javafx.collections.ObservableList
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.serialization.json.*
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class MainController : Controller(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx
    private val backend = Cirrus()

    fun connect(danmakuModel: DanmakuModel) {
        backend.messageHandler = configureHandler(danmakuModel)
        if (backend.runningJob()) {
            backend.stopAll()
        }
        backend.connectToBLive(92613)
    }

    private fun configureHandler(danmakuModel: DanmakuModel): MessageHandler {
        val container = danmakuModel.observableDanmakuList
        return rawMessageHandler {
            onMessage {
                handleMessage(it, container)
            }
        }
    }

    @Suppress("DuplicatedCode")
    private fun handleMessage(rawJsonStr: String, container: ObservableList<Danmaku>) {
        val root = Json.parseToJsonElement(rawJsonStr)
        val cmd = root.jsonObject["cmd"]?.jsonPrimitive?.content!!
        when (searchCMD(cmd)) {
            CMD.DANMU_MSG -> {
                val info = root.jsonObject["info"]!!.jsonArray
                val said = info[1].jsonPrimitive.content
                val id = info[2].jsonArray[0].jsonPrimitive.int
                val user = info[2].jsonArray[1].jsonPrimitive.content
                pushToVM(container, user, said, id)
            }
            CMD.SEND_GIFT -> {
                val data = root.jsonObject["data"]!!.jsonObject
                val id = data["uid"]!!.jsonPrimitive.int
                val user = data["uname"]!!.jsonPrimitive.content
                val num = data["num"]!!.jsonPrimitive.int
                val giftName = data["giftName"]!!.jsonPrimitive.content
                pushToVM(container, user, "$user 送出了 $num 个 $giftName", id)
            }
            CMD.WELCOME -> {
                val data = root.jsonObject["data"]!!.jsonObject
                val user = data["uname"]!!.jsonPrimitive.content
                val id = data["uid"]?.jsonPrimitive?.int // may not be correct
                pushToVM(container, user, "$user 进入了直播间", id)
            }
            CMD.WELCOME_GUARD -> {
                val data = root.jsonObject["data"]!!.jsonObject
                val user = data["username"]!!.jsonPrimitive.content
                val id = data["uid"]?.jsonPrimitive?.int // may not be correct
                pushToVM(container, user, "$user 进入了直播间", id)
            }
            CMD.INTERACT_WORD -> {
                val data = root.jsonObject["data"]!!.jsonObject
                val user = data["uname"]!!.jsonPrimitive.content
                val id = data["uid"]?.jsonPrimitive?.int
                pushToVM(container, user, "$user 进入了直播间", id)
            }
            else -> {
                LOG.debug { rawJsonStr }
            }
        }
    }

    private fun pushToVM(container: ObservableList<Danmaku>, user: String, content: String, id: Int? = null) {
        launch {
            withContext(currentCoroutineContext()) {
                container.queueAdd(Danmaku(id ?: 0, user, content))
            }
        }
    }

    private fun ObservableList<Danmaku>.queueAdd(danmaku: Danmaku) {
        if (size > 10) {
            removeAt(0)
        }
        add(danmaku)
    }
}
