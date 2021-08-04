package cyou.shinobi9.cirrus.ui.view

import cyou.shinobi9.cirrus.ui.controller.DebugController
import cyou.shinobi9.cirrus.ui.model.Danmaku
import cyou.shinobi9.cirrus.ui.model.DanmakuModel
import cyou.shinobi9.cirrus.ui.model.RoomModel
import javafx.scene.control.cell.TextFieldListCell
import javafx.util.StringConverter
import tornadofx.*

class DebugView : View("cirrus-ui-debug") {

    override val root = hbox()
    private val danmakuModel = DanmakuModel()
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
                            debugController.connectToBLive(roomModel.roomId, danmakuModel)
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

            listview(danmakuModel.danmakusProperty) {
                setCellFactory {
                    TextFieldListCell(
                        object : StringConverter<Danmaku>() {
                            override fun toString(danmaku: Danmaku?): String = danmaku?.let {
                                "${danmaku.user} : ${danmaku.said}"
                            } ?: "null"

                            override fun fromString(string: String?): Danmaku? = string?.let {
                                val split = it.split(" : ")
                                Danmaku(split[0], split[1])
                            }
                        }
                    )
                }
            }
        }
    }
}
