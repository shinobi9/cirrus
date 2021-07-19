package core

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.conf.CirrusConfig
import java.security.Security

fun main() {
    System.setProperty("io.ktor.random.secure.random.provider", "DRBG")
    Security.setProperty("securerandom.drbg.config", "HMAC_DRBG,SHA-512,256,pr_and_reseed")
    useDispatcherIO()
}

fun simple() {
    val cirrus = Cirrus()
    cirrus.connectToBLive(readLine()!!.toInt())
    Thread.sleep(10_000)
    println("ready to close")
    cirrus.close()
}

fun useDispatcherIO() {
    val cirrus = Cirrus(CirrusConfig(useDispatchersIO = false))
    cirrus.connectToBLive(readLine()!!.toInt())
    Thread.sleep(35_000)
    println("ready to close")
    cirrus.close()
}
// wss://tx-gz-live-comet-03.chat.bilibili.com:443/sub
// wss://ks-gz-live-comet-02.chat.bilibili.com:443/sub

// wss://tx-bj-live-comet-03.chat.bilibili.com:443/sub
