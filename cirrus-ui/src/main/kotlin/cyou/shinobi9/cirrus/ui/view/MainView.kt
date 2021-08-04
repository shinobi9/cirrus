package cyou.shinobi9.cirrus.ui.view

import cyou.shinobi9.cirrus.ui.controller.MainController
import cyou.shinobi9.cirrus.ui.model.Danmaku
import cyou.shinobi9.cirrus.ui.model.DanmakuModel
import cyou.shinobi9.cirrus.ui.model.RoomModel
import javafx.scene.control.cell.TextFieldListCell
import javafx.util.StringConverter
import tornadofx.*

class MainView : View("cirrus-ui") {

    override val root = hbox()
    private val danmakuModel = DanmakuModel()
    private val mainController by inject<MainController>()
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
                        setOnMouseClicked {
                            mainController.connectToBLive(roomModel.roomId, danmakuModel)
//                    model.danmakusProperty.add(Danmaku("shinobi", "you died"))
                        }
                    }
                    button("stop") {
                        setOnMouseClicked {
                            mainController.stop()
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
