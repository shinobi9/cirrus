package core

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.conf.CirrusConfig
import cyou.shinobi9.cirrus.handler.message.rawMessageHandler
import cyou.shinobi9.cirrus.handler.message.simpleMessageHandler

fun main() {
    rawMessage()
//    simpleMessage()
//    core.simpleMessage()
}

fun simpleMessage() {
    val cirrus = Cirrus(
        CirrusConfig(
            messageHandler = simpleMessageHandler {
                onReceiveDanmaku { user, said ->
                    println("$user : $said")
                }
            }
        )
    )
    cirrus.connectToBLive(1314)
    Thread.sleep(100_000)
    println("ready to close")
    cirrus.close()
}

fun rawMessage() {
    val cirrus = Cirrus(
        CirrusConfig(
            messageHandler = rawMessageHandler {
                onMessage {
                    println(it)
                }
            }
        )
    )
    cirrus.connectToBLive(1314)
    Thread.currentThread().join()
//    Thread.sleep(10_000)
//    println("ready to close")
//    cirrus.close()
}
