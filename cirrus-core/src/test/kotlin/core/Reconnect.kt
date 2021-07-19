package core

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.handler.event.simpleEventHandler
import cyou.shinobi9.cirrus.handler.message.simpleMessageHandler
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val room = readLine()!!.toInt()
        val cirrus = Cirrus(
            messageHandler = simpleMessageHandler {
                onReceiveDanmaku { user, said ->
                    TEST_LOG.info { "$user : $said" }
                }
                onUserEnterInLiveRoom {
                    TEST_LOG.info { "$it enter in" }
                }
            },
            eventHandler = simpleEventHandler {
                onDisconnect {
                    runBlocking {
                        TEST_LOG.info { "ready to reconnect" }
                        it.connectToBLive(room)
                    }
                }
            }
        )
        cirrus.connectToBLive(room)
    }
    Thread.currentThread().join()
}
