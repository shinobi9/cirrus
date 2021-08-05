package cyou.shinobi9.cirrus.ui

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.px

class Styles : Stylesheet() {
    companion object {
//        val heading by cssclass()
//        val fieldset by cssclass()
    }

    init {
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
