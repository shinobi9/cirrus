package cyou.shinobi9.cirrus.ui.view

import cyou.shinobi9.cirrus.network.packet.CMD.*
import cyou.shinobi9.cirrus.ui.controller.MainController
import cyou.shinobi9.cirrus.ui.extension.GiftInfo
import cyou.shinobi9.cirrus.ui.model.DanmakuListModel
import cyou.shinobi9.cirrus.ui.model.DanmakuModel
import cyou.shinobi9.cirrus.ui.model.LoginModel
import cyou.shinobi9.cirrus.ui.model.RoomModel
import javafx.beans.binding.ObjectBinding
import javafx.event.EventTarget
import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos.BOTTOM_LEFT
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.ButtonBar.ButtonData.LEFT
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Stage
import tornadofx.*

class MainView : View("cirrus-ui") {
    override val root = borderpane()
    private val mainController by inject<MainController>()
    private var xOffset = 0.0
    private var yOffset = 0.0
    private val danmakuListModel = DanmakuListModel()
    private val roomModel = RoomModel()
    private val loginModel = LoginModel()

    init {
        with(root) {
            center {
                vbox {
                    style {
                        padding = box(10.px)
                        alignment = BOTTOM_LEFT
                    }
                    dispatchDifferentTypeMessage()
                }
            }
            right {
                vbox {
                    style {
                        backgroundColor += Color.web("#000000", 0.1)
                    }
                    form {
                        fieldset(title) {
                            field("room id") {
                                textfield(roomModel.room.roomIdProp)
                            }
                        }
                        fieldset(labelPosition = VERTICAL) {
                            field {
                                buttonbar {
                                    button("start", type = LEFT) {
                                        action {
                                            mainController.connect(danmakuListModel, roomModel)
                                        }
                                    }
                                }
                            }
                            field {
                                buttonbar {
                                    button("stop", type = LEFT) {
                                        action {
                                            mainController.stop()
                                        }
                                    }
                                }
                            }
                            field {
                                buttonbar {
                                    button("clean cache", type = LEFT) {
                                        action {
                                            DanmakuModel.cacheManager.clearCache()
                                        }
                                    }
                                }
                            }
                            field {
                                buttonbar {
                                    button("exit", type = LEFT) {
                                        action {
                                            (scene.window as Stage).close()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    separator {
                        style {
                            padding = box(0.px, 10.px)
                        }
                    }
                    vbox {
                        style {
                            padding = box(10.px)
                            spacing = 5.px
                        }
                        button("login") {
                            action {
                                mainController.login(loginModel)
                            }
                        }
                        button("logout") {
                            action {
                                mainController.logout(loginModel)
                            }
                        }
                        imageview(loginModel.loginInfo.qrcodeProp)
                        label(loginModel.loginInfo.unameProp) {
                            style {
                                backgroundColor += Color.RED
                            }
                        }
                    }
                }
            }

            setOnMousePressed { event ->
                xOffset = event.sceneX
                yOffset = event.sceneY
            }
            setOnMouseDragged { event ->
                currentStage?.x = event.screenX - xOffset
                currentStage?.y = event.screenY - yOffset
            }
        }
    }

    override fun onDock() {
        currentStage?.scene?.fill = null
    }

    private fun EventTarget.dispatchDifferentTypeMessage() = bindChildren(danmakuListModel.danmakusProperty) {
        hbox {
            style {
                padding = box(2.px, 0.px)
            }
            with(it) {
                when (danmaku.type) {
                    DANMU_MSG -> avatarDanmaku(it.imageProp, "${danmaku.user} : ${danmaku.content}")
                    INTERACT_WORD -> avatarDanmaku(it.imageProp, "${danmaku.user} 进入了直播间")
                    SEND_GIFT -> {
                        val gift = danmaku.content as GiftInfo
                        avatarDanmaku(it.imageProp, "${danmaku.user} 送出了 ${gift.num} 个 ${gift.giftName}")
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun EventTarget.avatarDanmaku(avatarProp: ObjectBinding<Image>, text: String) = hbox {
        style {
            alignment = CENTER_LEFT
            spacing = 5.px
//            borderColor += box(Color.RED)
        }
        imageview(avatarProp) {
            fitWidth = 30.0
            fitHeight = 30.0
            clip = Circle(15.0, 15.0, 15.0, Color.AQUA)
        }
        label(text) {
            textFill = Color.WHITE
        }
    }
}
