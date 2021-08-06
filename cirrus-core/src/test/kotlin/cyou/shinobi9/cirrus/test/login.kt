package cyou.shinobi9.cirrus.test

import cyou.shinobi9.cirrus.defaultCookiesClient
import cyou.shinobi9.cirrus.network.accountInfo
import cyou.shinobi9.cirrus.network.applyForLoginQrcode
import cyou.shinobi9.cirrus.network.loginInfo
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
//    """https://passport.biligame.com/crossDomain?DedeUserID=3220953&DedeUserID__ckMd5=e1f0740e575c17a6&Expires=15551000&SESSDATA=c99d7239%2C1643771163%2C2d12c%2A81&bili_jct=743eb60127a22a6c782392dc66e21987&gourl=http%3A%2F%2Fwww.bilibili.com"""
//        .decodeURLQueryComponent().also { println(it) }
//    Unit
    defaultCookiesClient.login()
    println(defaultCookiesClient.accountInfo())
}


suspend fun HttpClient.login() {
    val (url, oauthKey) = applyForLoginQrcode()
    println(url)
    while (true) {
        delay(2000)
        val (success, data) = loginInfo(oauthKey)
        println(success)
        println(data)
        if (success) {
            val decode = (data as String).decodeURLQueryComponent()
            println(decode)
            get<String>(decode).also { println(it) }
            break
        }
    }
}
