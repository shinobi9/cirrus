package core

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.handler.message.rawMessageHandler
import cyou.shinobi9.cirrus.handler.message.simpleMessageHandler

fun main() {
    println("raw ?(y/n)")
    if (readLine()!! == "y") rawMessage() else simpleMessage()
}

fun simpleMessage() {
    val cirrus = Cirrus(
        messageHandler = simpleMessageHandler {
            onReceiveDanmaku { user, said ->
                println("$user : $said")
            }
            onUserEnterInLiveRoom {
                println("$it enter in")
            }
        }
    )
    cirrus.connectToBLive(readLine()!!.toInt())
    Thread.currentThread().join()
}

fun rawMessage() {
    val cirrus = Cirrus(
        messageHandler = rawMessageHandler {
            onMessage {
                println(it)
            }
        }
    )
    cirrus.connectToBLive(readLine()!!.toInt())
    Thread.currentThread().join()
}
