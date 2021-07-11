package cyou.shinobi9.cirrus.ui

import cyou.shinobi9.cirrus.ui.view.MainView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import mu.KotlinLogging
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class CirrusUIApplication: App(MainView::class, Styles::class),CoroutineScope{
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx

}

fun main(args: Array<String>) = launch<CirrusUIApplication>(args)

internal val LOG = KotlinLogging.logger {}