package cyou.shinobi9.cirrus.conf

import cyou.shinobi9.cirrus.handler.event.EventHandler
import cyou.shinobi9.cirrus.handler.event.simpleEventHandler
import cyou.shinobi9.cirrus.handler.message.MessageHandler
import cyou.shinobi9.cirrus.handler.message.rawMessageHandler
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*

val defaultClient = HttpClient(CIO) {
    BrowserUserAgent()
    install(WebSockets)
    install(Logging) {
        level = LogLevel.ALL
    }
    install(JsonFeature) {
        serializer = KotlinxSerializer(
            kotlinx.serialization.json.Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
}

data class CirrusConfig(
    val httpClient: HttpClient = defaultClient,
    val threadsCount: Int = 10,
    val useDispatchersIO: Boolean = false,
    val eventHandler: EventHandler = simpleEventHandler {},
    val messageHandler: MessageHandler = rawMessageHandler {}
)
