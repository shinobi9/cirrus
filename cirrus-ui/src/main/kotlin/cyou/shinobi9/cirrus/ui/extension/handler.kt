package cyou.shinobi9.cirrus.ui.extension

import cyou.shinobi9.cirrus.network.packet.CMD.*
import cyou.shinobi9.cirrus.network.packet.searchCMD
import cyou.shinobi9.cirrus.ui.LOG
import cyou.shinobi9.cirrus.ui.model.Danmaku
import cyou.shinobi9.cirrus.ui.model.DanmakuModel
import javafx.collections.ObservableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

data class GiftInfo(
    val giftName: String,
    val num: Int,

)

@Suppress("DuplicatedCode")
internal suspend fun handleMessage(rawJsonStr: String, container: ObservableList<DanmakuModel>) {
    val root = Json.parseToJsonElement(rawJsonStr)
    val cmd = root.jsonObject["cmd"]?.jsonPrimitive?.content!!
    when (searchCMD(cmd)) {
        DANMU_MSG -> {
            val info = root.jsonObject["info"]!!.jsonArray
            val said = info[1].jsonPrimitive.content
            val id = info[2].jsonArray[0].jsonPrimitive.int
            val user = info[2].jsonArray[1].jsonPrimitive.content
            pushToVM(container, Danmaku(id, user, said, DANMU_MSG))
        }
        SEND_GIFT -> {
            val data = root.jsonObject["data"]!!.jsonObject
            val id = data["uid"]?.jsonPrimitive?.int!!
            val user = data["uname"]!!.jsonPrimitive.content
            val num = data["num"]!!.jsonPrimitive.int
            val giftName = data["giftName"]!!.jsonPrimitive.content
            pushToVM(container, Danmaku(id, user, GiftInfo(giftName, num), SEND_GIFT))
        }
        WELCOME -> {
            val data = root.jsonObject["data"]!!.jsonObject
            val user = data["uname"]!!.jsonPrimitive.content
            val id = data["uid"]?.jsonPrimitive?.int // may not be correct
            pushToVM(container, Danmaku(id ?: 0, user, "", WELCOME))
        }
        WELCOME_GUARD -> {
            val data = root.jsonObject["data"]!!.jsonObject
            val user = data["username"]!!.jsonPrimitive.content
            val id = data["uid"]?.jsonPrimitive?.int // may not be correct
            pushToVM(container, Danmaku(id ?: 0, user, "", WELCOME_GUARD))
        }
        INTERACT_WORD -> {
            val data = root.jsonObject["data"]!!.jsonObject
            val user = data["uname"]!!.jsonPrimitive.content
            val id = data["uid"]?.jsonPrimitive?.int!!
            pushToVM(container, Danmaku(id, user, "", INTERACT_WORD))
        }
        else -> {
            LOG.debug { rawJsonStr }
        }
    }
}

internal suspend fun pushToVM(container: ObservableList<DanmakuModel>, danmaku: Danmaku) {
    withContext(Dispatchers.JavaFx) {
        container.queueAdd(DanmakuModel(danmaku))
    }
}

internal fun ObservableList<DanmakuModel>.queueAdd(danmakuModel: DanmakuModel) {
    if (size > 10) {
        val removed = removeAt(0)
        removed.danmaku.extra.clear()
    }
    add(danmakuModel)
}
