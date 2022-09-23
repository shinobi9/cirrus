@file:Suppress("SpellCheckingInspection")

package cyou.shinobi9.cirrus.ui

import javafx.scene.layout.BackgroundRepeat
import javafx.scene.paint.Color.*
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
//        val heading by cssclass()
//        val fieldset by cssclass()
    }

    init {

        root {
            //                backgroundColor += Color.TRANSPARENT
            backgroundColor += web("#000000", 0.2)
            backgroundRepeat += BackgroundRepeat.NO_REPEAT to BackgroundRepeat.NO_REPEAT
            prefWidth = 600.px
            prefHeight = 500.px
        }

        label {
//            padding = box(10.px)
//            fontSize = 20.px
//            fontWeight = FontWeight.BOLD
            textFill = WHITE
        }
        button {
            prefWidth = 150.px
            backgroundColor += GRAY
            textFill = WHITE
        }
        textField {
            prefWidth = 80.px
            backgroundColor += GRAY
            textFill = WHITE
        }

        listView {
//            borderColor += box(RED)
            backgroundColor += TRANSPARENT
        }

        scrollBar {
            scaleX = 0
            scaleY = 0
        }

        listCell {
            padding = box(2.px, 0.px)
            backgroundColor += TRANSPARENT
        }
    }
}
