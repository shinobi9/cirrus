package cyou.shinobi9.cirrus

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import java.security.Security


fun main() {
    System.setProperty("io.ktor.random.secure.random.provider", "DRBG")
    Security.setProperty("securerandom.drbg.config", "HMAC_DRBG,SHA-512,256,pr_and_reseed")
    runBlocking {
        val client = HttpClient(CIO).config {
            Logging { level = LogLevel.ALL }
            install(WebSockets)
            BrowserUserAgent() // install default browser-like user-agent
            // install(UserAgent) { agent = "some user agent" }
        }
        client.ws(
            host = "localhost",
            port = 8080,
            path = "/",
            request = {
                headers["origin"] = "http://localhost"
            }
        ) {
            val a = launch {
                while (true) {
                    println(Thread.currentThread().name + "   send")
                    outgoing.send(Frame.Text("asd"))
                    delay(1000)
                }
            }
            val b = launch {
                while (true) {
                    val message = incoming.receive()
                    if (message is Frame.Text) {
                        println("Server said: " + message.readText())
                    }
                }
            }
            joinAll(a, b)
        }
    }
}