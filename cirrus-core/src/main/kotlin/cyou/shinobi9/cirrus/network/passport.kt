package cyou.shinobi9.cirrus.network

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * qrcode will expire in 180s
 * return Pair<url,oauthKey>
 */
suspend fun HttpClient.applyForLoginQrcode(): Pair<String, String> {
    val response = get<JsonElement>("https://passport.bilibili.com/qrcode/getLoginUrl")
    val data = response.jsonObject["data"]?.jsonObject ?: jsonResolveError("data")
    val url = data["url"]?.jsonPrimitive?.content ?: jsonResolveError("url")
    val oauthKey = data["oauthKey"]?.jsonPrimitive?.content ?: jsonResolveError("oauthKey")
    return url to oauthKey
}

/**
 * oauthKey will expire in 180s
 * result :
 *
 * false to LoginInfoCode
 *
 * true to url
 */
suspend fun HttpClient.loginInfo(oauthKey: String): Pair<Boolean, Any> {
    val response = post<JsonElement>("https://passport.bilibili.com/qrcode/getLoginInfo") {
        parameter("oauthKey", oauthKey)
    }.jsonObject
    val status = response["status"]?.jsonPrimitive?.boolean ?: jsonResolveError("status")
    val data = response.jsonObject["data"]

    return if (status) {
        val url = data?.jsonObject?.get("url")?.jsonPrimitive?.content ?: jsonResolveError("url")
        true to url
    } else {
        val code = data?.jsonPrimitive?.intOrNull?.let { LoginInfoCode.values().find { code -> code.code == it } }
            ?: jsonResolveError("data")
        false to code
    }
}

/**
 * require cookies
 */
suspend fun HttpClient.accountInfo(): AccountInfo {
    val response = get<SimpleWrap<AccountInfo>>("https://api.bilibili.com/x/member/web/account")
    return response.data
}

/**
 * -1：密钥错误
 * -2：密钥超时
 * -4：未扫描
 * -5：未确认
 */
enum class LoginInfoCode(val code: Int) {
    ERROR_KEY(-1),
    ERROR_TIMEOUT(-2),
    NOT_SCAN(-4),
    NOT_CONFIRM(-5),
}


@Serializable
data class AccountInfo(
    val birthday: String,
    val mid: Int,
    val nick_free: Boolean,
    val rank: String,
    val sex: String,
    val sign: String,
    val uname: String,
    val userid: String
)