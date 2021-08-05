@file:Suppress("unused")

package cyou.shinobi9.cirrus.ui.model

import cyou.shinobi9.cirrus.network.packet.CMD
import cyou.shinobi9.cirrus.ui.cache.CacheManager
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
    val imageProp = nonNullObjectBinding(danmaku.id) {
        Image(cacheManager.resolveAvatar(this), 50.0, 50.0, false, false)
    }

    companion object {
        val cacheManager: CacheManager = CacheManager()
    }
}

class DanmakuListModel(
    val observableDanmakuList: ObservableList<DanmakuModel> = mutableListOf<DanmakuModel>().asObservable(),
) : ViewModel() {
    val danmakusProperty: ListProperty<DanmakuModel> = listProperty(observableDanmakuList)
}
