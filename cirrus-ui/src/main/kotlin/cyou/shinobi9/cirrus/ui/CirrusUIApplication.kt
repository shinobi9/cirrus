package cyou.shinobi9.cirrus.ui

import cyou.shinobi9.cirrus.ui.cache.CacheManager
import cyou.shinobi9.cirrus.ui.view.DebugView
import cyou.shinobi9.cirrus.ui.view.MainView
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import javafx.event.EventHandler
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.WindowEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import tornadofx.*
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
        stage.isAlwaysOnTop = true
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

internal val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
}
internal val defaultClient = HttpClient(CIO) {
    BrowserUserAgent()
    install(HttpRedirect) {
        allowHttpsDowngrade = true
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 5000
    }
    install(Logging) {
        level = LogLevel.ALL
    }
    install(JsonFeature) {
        serializer = KotlinxSerializer(json)
    }
    engine {
        proxy = ProxyBuilder.http("http://127.0.0.1:7890")
    }
}

internal val defaultCookiesClient = defaultClient.config {
    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }
}

val cacheManager: CacheManager = CacheManager()
