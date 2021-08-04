package cyou.shinobi9.cirrus.ui.view

import cyou.shinobi9.cirrus.ui.controller.MainController
import cyou.shinobi9.cirrus.ui.model.DanmakuModel
import javafx.geometry.Pos
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.stage.Stage
import tornadofx.*

class MainView : View("cirrus-ui") {
    override val root = borderpane()
    private val mainController by inject<MainController>()
    private val danmakuModel = DanmakuModel()
    private var xOffset = 0.0
    private var yOffset = 0.0

    init {
        with(root) {
            style {
//                backgroundColor += Color.TRANSPARENT
                backgroundColor += Color.web("#000000", 0.2)
                backgroundRepeat += BackgroundRepeat.NO_REPEAT to BackgroundRepeat.NO_REPEAT
                prefWidth = 600.px
                prefHeight = 400.px
            }
            center {
                vbox {
                    style {
                        alignment = Pos.BOTTOM_LEFT
                    }
                    bindChildren(danmakuModel.danmakusProperty) {
                        label("${it.user} : ${it.said}") {
                            textFill = Paint.valueOf("white")
                        }
                    }
                }
            }
            right {
                vbox {
                    button("start") {
                        setOnAction {
                            mainController.connect(danmakuModel)
                        }
                    }
                    button("exit") {
                        setOnAction {
                            (scene.window as Stage).close()
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
}
