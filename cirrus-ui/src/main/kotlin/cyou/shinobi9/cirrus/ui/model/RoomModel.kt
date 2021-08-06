package cyou.shinobi9.cirrus.ui.model

import javafx.beans.property.IntegerProperty
import tornadofx.*

class RoomModel(val room: Room = Room()) : ViewModel() {
    var roomId by room.roomIdProp
    var popularity by room.popularityProp

    data class Room(
        val roomIdProp: IntegerProperty = intProperty(-1),
        val popularityProp: IntegerProperty = intProperty(0)
    ) {
        val popularityDescProp = stringBinding(popularityProp) {
            "人气值: $value"
        }
    }
}
