@file:Suppress("unused")

package cyou.shinobi9.cirrus.ui.model

import cyou.shinobi9.cirrus.network.packet.CMD
import cyou.shinobi9.cirrus.network.packet.CMD.*
import cyou.shinobi9.cirrus.ui.cacheManager
import cyou.shinobi9.cirrus.ui.extension.GiftInfo
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import javafx.scene.image.Image
import tornadofx.*

data class DebugDanmaku(
    var user: String,
    var said: String
)

class DebugDanmakuModel(val observableDebugDanmakuList: ObservableList<DebugDanmaku> = mutableListOf<DebugDanmaku>().asObservable()) :
    ViewModel() {
    val danmakusProperty: ListProperty<DebugDanmaku> = listProperty(observableDebugDanmakuList)
}

data class Danmaku(
    val id: Int,
    val user: String,
    val content: Any?,
    val type: CMD,
    val extra: MutableMap<String, Any?> = mutableMapOf(),
)

class DanmakuModel(
    val danmaku: Danmaku,
) :
    ItemViewModel<Danmaku>() {
    fun doClear() {
        danmaku.extra.clear()
    }

    val imageProp = objectBinding(danmaku.id) {
        if (this == 0) null else
            Image(
                cacheManager.resolveAvatar(this), 30.0, 30.0, true, true, true
            )
    }
    val danmakuContentProp = objectBinding(danmaku.content ?: "") {
        when (danmaku.type) {
            DANMU_MSG -> "${danmaku.user} : ${danmaku.content}"
            INTERACT_WORD -> "${danmaku.user} 进入了直播间"
            SEND_GIFT -> {
                val gift = danmaku.content as GiftInfo
                "${danmaku.user} 送出了 ${gift.num} 个 ${gift.giftName}"
            }
            else -> {
                ""
            }
        }
    }
}

const val MAX_SIZE = 10

class DanmakuListModel(
    val observableDanmakuList: ObservableList<DanmakuModel> = mutableListOf<DanmakuModel>().apply {
        repeat(MAX_SIZE) {
            add(DanmakuModel(Danmaku(0, "", MAX_SIZE, UNKNOWN)))
        }
    }.asObservable(),
    @Suppress("MemberVisibilityCanBePrivate")
    val showAvatarProp: BooleanProperty = booleanProperty(true),

) : ViewModel() {
    var showAvatar: Boolean by showAvatarProp
    val showAvatarDescBinding = stringBinding(showAvatarProp) {
        if (value) "hide avatar" else "show avatar"
    }

    val danmakusProperty: ListProperty<DanmakuModel> = listProperty(observableDanmakuList)
}

internal fun ObservableList<DanmakuModel>.queueAdd(danmakuModel: DanmakuModel) {
    repeat(MAX_SIZE) {
        moveDownAt(it)
    }
    removeLast()
    add(danmakuModel)
}
