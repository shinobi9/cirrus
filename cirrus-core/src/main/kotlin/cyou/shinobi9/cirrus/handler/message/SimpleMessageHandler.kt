package cyou.shinobi9.cirrus.handler.message

import cyou.shinobi9.cirrus.json
import cyou.shinobi9.cirrus.network.packet.CMD.*
import cyou.shinobi9.cirrus.network.packet.searchCMD
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

interface SimpleMessageHandler : MessageHandler {
    fun onReceiveDanmaku(block: (user: String, said: String) -> Unit)
    fun onReceiveGift(block: (user: String, num: Int, giftName: String) -> Unit)
    fun onUserEnterInLiveRoom(block: (user: String) -> Unit)
    fun onVipEnterInLiveRoom(block: (user: String) -> Unit)
    fun onGuardEnterInLiveRoom(block: (user: String) -> Unit)
    fun onAllTypeMessage(block: (message: String) -> Unit)
    fun onUnknownTypeMessage(block: (message: String) -> Unit)
    fun onHeartBeat(block: (number: Int) -> Unit)
    fun onError(block: (message: String, e: MessageException) -> Unit)
}

class SimpleMessageHandlerImpl(
    private var receiveDanmaku: ((user: String, said: String) -> Unit)? = null,
    private var receiveGift: ((user: String, num: Int, giftName: String) -> Unit)? = null,
    private var userEnterInLiveRoom: ((user: String) -> Unit)? = null,
    private var vipEnterInLiveRoom: ((user: String) -> Unit)? = null,
    private var guardEnterInLiveRoom: ((user: String) -> Unit)? = null,
    private var allTypeMessage: ((message: String) -> Unit)? = null,
    private var unknownTypeMessage: ((message: String) -> Unit)? = null,
    private var heartBeat: ((number: Int) -> Unit)? = null,
    private var error: ((message: String, e: MessageException) -> Unit)? = null
) : SimpleMessageHandler {
    override fun onReceiveDanmaku(block: (user: String, said: String) -> Unit) {
        receiveDanmaku = block
    }

    override fun onReceiveGift(block: (user: String, num: Int, giftName: String) -> Unit) {
        receiveGift = block
    }

    override fun onUserEnterInLiveRoom(block: (user: String) -> Unit) {
        userEnterInLiveRoom = block
    }

    override fun onVipEnterInLiveRoom(block: (user: String) -> Unit) {
        vipEnterInLiveRoom = block
    }

    override fun onGuardEnterInLiveRoom(block: (user: String) -> Unit) {
        guardEnterInLiveRoom = block
    }

    override fun onAllTypeMessage(block: (message: String) -> Unit) {
        allTypeMessage = block
    }

    override fun onUnknownTypeMessage(block: (message: String) -> Unit) {
        unknownTypeMessage = block
    }

    override fun onHeartBeat(block: (number: Int) -> Unit) {
        heartBeat = block
    }

    override fun onError(block: (message: String, e: MessageException) -> Unit) {
        error = block
    }

    override fun handle(message: String) {
        try {
            allTypeMessage?.invoke(message)
            val root = json.parseToJsonElement(message)
            val cmd = root.jsonObject["cmd"]?.jsonPrimitive?.content
                ?: throw Exception("unexpect json format, missing [cmd] !")
            when (searchCMD(cmd)) {
                DANMU_MSG -> {
                    val info = root.jsonObject["info"]!!.jsonArray
                    val said = info[1].jsonPrimitive.content
                    val user = info[2].jsonArray[1].jsonPrimitive.content
                    receiveDanmaku?.invoke(user, said)
                }
                SEND_GIFT -> {
                    val data = root.jsonObject["data"]!!.jsonObject
                    val user = data["uname"]!!.jsonPrimitive.content
                    val num = data["num"]!!.jsonPrimitive.int
                    val giftName = data["giftName"]!!.jsonPrimitive.content
                    receiveGift?.invoke(user, num, giftName)
                }
                WELCOME -> {
                    val user = root.jsonObject["data"]!!.jsonObject["uname"]!!.jsonPrimitive.content
                    vipEnterInLiveRoom?.invoke(user)
                }
                WELCOME_GUARD -> {
                    val user = root.jsonObject["data"]!!.jsonObject["username"]!!.jsonPrimitive.content
                    guardEnterInLiveRoom?.invoke(user)
                }
                INTERACT_WORD -> {
                    val user = root.jsonObject["data"]!!.jsonObject["uname"]!!.jsonPrimitive.content
                    userEnterInLiveRoom?.invoke(user)
                }
                UNKNOWN -> {
                    unknownTypeMessage?.invoke(message)
                }
                else -> {
                }
            }
        } catch (e: Throwable) {
            error?.invoke(message, MessageException("catch an exception while handling a message : $message", e))
        }
    }

    override fun handleHeartBeat(number: Int) {
        heartBeat?.invoke(number)
    }
}

class MessageException(message: String, e: Throwable) : RuntimeException(message, e)

inline fun simpleMessageHandler(content: SimpleMessageHandler.() -> Unit) = SimpleMessageHandlerImpl().apply(content)
