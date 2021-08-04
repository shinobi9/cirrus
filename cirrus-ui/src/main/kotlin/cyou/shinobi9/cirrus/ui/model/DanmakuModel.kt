@file:Suppress("unused")

package cyou.shinobi9.cirrus.ui.model

import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
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
    var id: Int,
    var user: String,
    var said: String
)

class DanmakuModel(val observableDanmakuList: ObservableList<Danmaku> = mutableListOf<Danmaku>().asObservable()) :
    ViewModel() {
    val danmakusProperty: ListProperty<Danmaku> = listProperty(observableDanmakuList)
}
