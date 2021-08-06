@file:Suppress("SpellCheckingInspection")

package cyou.shinobi9.cirrus.ui

import javafx.scene.layout.BackgroundRepeat
import javafx.scene.paint.Color
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
//        val heading by cssclass()
//        val fieldset by cssclass()
    }

    init {

        root {
            //                backgroundColor += Color.TRANSPARENT
            backgroundColor += Color.web("#000000", 0.2)
            backgroundRepeat += BackgroundRepeat.NO_REPEAT to BackgroundRepeat.NO_REPEAT
            prefWidth = 600.px
            prefHeight = 500.px
        }

        label {
//            padding = box(10.px)
//            fontSize = 20.px
//            fontWeight = FontWeight.BOLD
            textFill = Color.WHITE
        }
        button {
            prefWidth = 150.px
            backgroundColor += Color.GRAY
            textFill = Color.WHITE
        }
        textField {
            prefWidth = 80.px
            backgroundColor += Color.GRAY
            textFill = Color.WHITE
        }
    }
}
