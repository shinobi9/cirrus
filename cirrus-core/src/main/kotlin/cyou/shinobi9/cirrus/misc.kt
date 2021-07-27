package cyou.shinobi9.cirrus

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import mu.KotlinLogging

internal val LOG = KotlinLogging.logger { }
internal val defaultClient = HttpClient(CIO) {
    BrowserUserAgent()
    install(WebSockets)
    install(HttpTimeout) {
        connectTimeoutMillis = 5000
    }
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
