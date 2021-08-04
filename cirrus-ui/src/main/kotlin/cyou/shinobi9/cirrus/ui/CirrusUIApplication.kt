package cyou.shinobi9.cirrus.ui

import cyou.shinobi9.cirrus.ui.view.DebugView
import cyou.shinobi9.cirrus.ui.view.MainView
import javafx.event.EventHandler
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.WindowEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import mu.KotlinLogging
import tornadofx.App
import tornadofx.launch
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

internal val closeHandler = EventHandler<WindowEvent> {
    LOG.debug { "exit" }
    exitProcess(0)
}

class CirrusUIDebugApplication : App(DebugView::class, Styles::class), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx

    override fun start(stage: Stage) {
        stage.onCloseRequest = closeHandler
        super.start(stage)
    }
}

class CirrusUIApplication : App(MainView::class, Styles::class), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx

    override fun start(stage: Stage) {
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.onCloseRequest = closeHandler
        stage.onHidden = closeHandler
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    if (args.contains("--debug")) {
        launch<CirrusUIDebugApplication>(args)
    } else {
        launch<CirrusUIApplication>(args)
    }
}

internal val LOG = KotlinLogging.logger {}
