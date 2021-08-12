package cyou.shinobi9.cirrus.ui.view

import cyou.shinobi9.cirrus.network.packet.CMD.*
import cyou.shinobi9.cirrus.ui.cacheManager
import cyou.shinobi9.cirrus.ui.controller.MainController
import cyou.shinobi9.cirrus.ui.extension.GiftInfo
import cyou.shinobi9.cirrus.ui.model.DanmakuListModel
import cyou.shinobi9.cirrus.ui.model.DanmakuModel
import cyou.shinobi9.cirrus.ui.model.LoginModel
import cyou.shinobi9.cirrus.ui.model.RoomModel
import javafx.collections.ListChangeListener
import javafx.event.EventTarget
import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos.*
import javafx.scene.control.ButtonBar.ButtonData.LEFT
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.paint.Color.*
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
                hbox {
                    vbox {
                        style {
                            padding = box(10.px)
                            alignment = TOP_LEFT
                        }
                        label(roomModel.room.popularityDescProp)
                        vbox {
                            style {
//                                padding = box(10.px)
                                prefHeight = 600.px
                                alignment = BOTTOM_LEFT
                                spacing = 10.px
                            }
                            button("clear") {
                                action {
                                    danmakuListModel.observableDanmakuList.clear()
                                }
                            }
                            separator()
                            listview(danmakuListModel.observableDanmakuList) {
                                style {
                                    prefHeight = 200.px
                                    prefWidth = 400.px
                                    alignment = BOTTOM_LEFT
                                }
                                setCellFactory { DanmakuListCell(danmakuListModel) }
                                items.addListener(
                                    ListChangeListener {
                                        while (it.next()) {
                                            if (it.wasAdded()) {
                                                scrollTo(Int.MAX_VALUE)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            right {
                vbox {
                    style {
                        backgroundColor += web("#000000", 0.1)
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
                                            cacheManager.clearCache()
                                        }
                                    }
                                }
                            }
                            field {
                                buttonbar {
                                    button(danmakuListModel.showAvatarDescBinding, type = LEFT) {
                                        action {
                                            danmakuListModel.showAvatar = !danmakuListModel.showAvatar
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
                            hiddenWhen(loginModel.loginInfo.loginProp)
                            managedProperty().bind(visibleProperty())
                            action {
                                mainController.login(loginModel)
                            }
                        }
                        button("logout") {
                            visibleWhen(loginModel.loginInfo.loginProp)
                            managedProperty().bind(visibleProperty())
                            action {
                                mainController.logout(loginModel)
                            }
                        }
                        imageview(loginModel.loginInfo.qrcodeProp)
                        label(loginModel.loginInfo.unameProp)
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

    class DanmakuListCell(private val danmakuListModel: DanmakuListModel) : ListCell<DanmakuModel>() {
        private fun EventTarget.avatarDanmaku(model: DanmakuModel, text: String, showAvatar: Boolean) = hbox {
            style {
                alignment = CENTER_LEFT
//                spacing = 5.px
//                borderColor += box(BLUE)
            }
            if (showAvatar) {
                imageview {
                    image = Image(cacheManager.resolveAvatar(model.danmaku.id), 30.0, 30.0, true, true, true)
                    fitWidth = 30.0
                    fitHeight = 30.0
                    clip = Circle(15.0, 15.0, 15.0, AQUA)
                }
            }
            label(text) {
                textFill = WHITE
            }
        }

        private fun DanmakuModel.toHBoxItem(): HBox {
            val model = this@toHBoxItem
            return hbox {
                with(danmaku) {
                    val show = danmakuListModel.showAvatar
                    when (type) {
                        DANMU_MSG -> avatarDanmaku(model, "$user : $content", show)
                        INTERACT_WORD -> avatarDanmaku(model, "$user 进入了直播间", show)
                        SEND_GIFT -> {
                            val gift = content as GiftInfo
                            avatarDanmaku(model, "$user 送出了 ${gift.num} 个 ${gift.giftName}", show)
                        }
                        else -> {
                        }
                    }
                }
            }
        }

        override fun updateItem(item: DanmakuModel?, empty: Boolean) {
            super.updateItem(item, empty)

            graphic = if (empty || item == null) {
                null
            } else {
                val text = item.toHBoxItem()
                text
            }
        }
    }

    override fun onDock() {
        currentStage?.scene?.fill = null
    }
}
