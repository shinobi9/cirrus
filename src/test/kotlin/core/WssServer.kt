package core

import cyou.shinobi9.cirrus.conf.defaultClient
import cyou.shinobi9.cirrus.network.loadBalanceWebsocketServer
import kotlinx.coroutines.runBlocking

fun main() {

    test()
}

fun test() {
    runBlocking {
        println(defaultClient.loadBalanceWebsocketServer(544786))
        println("sad")
    }
}
