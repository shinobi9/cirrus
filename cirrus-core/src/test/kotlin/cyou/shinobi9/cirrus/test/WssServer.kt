package cyou.shinobi9.cirrus.test

import cyou.shinobi9.cirrus.defaultClient
import cyou.shinobi9.cirrus.network.loadBalanceWebsocketServer
import kotlinx.coroutines.runBlocking

fun main() {

    test()
}

fun test() {
    runBlocking {
        println(defaultClient.loadBalanceWebsocketServer(readLine()!!.toInt()))
        println("sad")
    }
}
