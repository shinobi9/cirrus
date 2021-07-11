package cyou.shinobi9.cirrus.ui.model

import javafx.beans.property.IntegerProperty
import tornadofx.*

class RoomModel(val room: Room = Room()) : ViewModel() {
    var roomId by room.roomIdProp

    data class Room(
        val roomIdProp: IntegerProperty = intProperty(-1)
    )
}
