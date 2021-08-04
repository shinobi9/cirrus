package cyou.shinobi9.cirrus.ui

import cyou.shinobi9.cirrus.ui.view.MainView
import javafx.event.EventHandler
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import mu.KotlinLogging
import tornadofx.*
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

class CirrusUIApplication : App(MainView::class, Styles::class), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx

    override fun start(stage: Stage) {
        stage.onCloseRequest = EventHandler {
            exitProcess(0)
        }
        super.start(stage)
    }
}

fun main(args: Array<String>) = launch<CirrusUIApplication>(args)

internal val LOG = KotlinLogging.logger {}
