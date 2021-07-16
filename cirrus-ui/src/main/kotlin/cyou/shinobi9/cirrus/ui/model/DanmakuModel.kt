package cyou.shinobi9.cirrus.ui.model

import javafx.beans.property.ListProperty
import javafx.collections.ObservableList
import tornadofx.*

data class Danmaku(
    var user: String,
    var said: String
)

class DanmakuModel(val observableDanmakuList: ObservableList<Danmaku> = mutableListOf<Danmaku>().asObservable()) :
    ViewModel() {
    val danmakusProperty: ListProperty<Danmaku> = listProperty(observableDanmakuList)
}
