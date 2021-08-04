package cyou.shinobi9.cirrus.ui.view

import cyou.shinobi9.cirrus.ui.controller.DebugController
import cyou.shinobi9.cirrus.ui.model.DebugDanmaku
import cyou.shinobi9.cirrus.ui.model.DebugDanmakuModel
import cyou.shinobi9.cirrus.ui.model.RoomModel
import javafx.scene.control.cell.TextFieldListCell
import javafx.util.StringConverter
import tornadofx.*

class DebugView : View("cirrus-ui-debug") {

    override val root = hbox()
    private val debugDanmakuModel = DebugDanmakuModel()
    private val debugController by inject<DebugController>()
    private val roomModel = RoomModel()

    init {
        with(root) {
            style {
                padding = box(10.px)
            }
            form {
                vbox {
                    style {
                        spacing = 10.px
                    }
                    label("type room id")
                    textfield(roomModel.room.roomIdProp)
                    button("connect") {
                        setOnAction {
                            debugController.connectToBLive(roomModel.roomId, debugDanmakuModel)
//                    model.danmakusProperty.add(Danmaku("shinobi", "you died"))
                        }
                    }
                    button("stop") {
                        setOnAction {
                            debugController.stop()
                        }
                    }
                }
            }

            listview(debugDanmakuModel.danmakusProperty) {
                setCellFactory {
                    TextFieldListCell(
                        object : StringConverter<DebugDanmaku>() {
                            override fun toString(danmaku: DebugDanmaku?): String = danmaku?.let {
                                "${danmaku.user} : ${danmaku.said}"
                            } ?: "null"

                            override fun fromString(string: String?): DebugDanmaku? = string?.let {
                                val split = it.split(" : ")
                                DebugDanmaku(split[0], split[1])
                            }
                        }
                    )
                }
            }
        }
    }
}
