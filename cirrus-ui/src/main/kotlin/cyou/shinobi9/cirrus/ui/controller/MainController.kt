package cyou.shinobi9.cirrus.ui.controller

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.handler.message.MessageHandler
import cyou.shinobi9.cirrus.handler.message.rawMessageHandler
import cyou.shinobi9.cirrus.network.accountInfo
import cyou.shinobi9.cirrus.network.applyForLoginQrcode
import cyou.shinobi9.cirrus.network.loginInfo
import cyou.shinobi9.cirrus.ui.LOG
import cyou.shinobi9.cirrus.ui.defaultCookiesClient
import cyou.shinobi9.cirrus.ui.extension.handleMessage
import cyou.shinobi9.cirrus.ui.model.DanmakuListModel
import cyou.shinobi9.cirrus.ui.model.LoginModel
import cyou.shinobi9.cirrus.ui.model.RoomModel
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class MainController : Controller(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job + loginJob
    private val backend = Cirrus()
    private val job = SupervisorJob()
    private val loginJob = SupervisorJob()
    fun connect(danmakuListModel: DanmakuListModel, roomModel: RoomModel) {
        backend.messageHandler = configureHandler(danmakuListModel, roomModel)
        if (backend.runningJob()) {
            backend.stopAll()
        }
        backend.connectToBLive(roomModel.roomId)
    }

    private fun configureHandler(danmakuListModel: DanmakuListModel, roomModel: RoomModel): MessageHandler {
        val container = danmakuListModel.observableDanmakuList
        return rawMessageHandler {
            onMessage {
                launch { handleMessage(it, container) }
            }
            onHeartBeat {
                launch {
                    withContext(Dispatchers.JavaFx) {
                        roomModel.popularity = it
                    }
                }
            }
        }
    }

    fun login(loginModel: LoginModel) {
        if (loginModel.login) {
            LOG.debug { "has login , ignore" }
            return
        }
        launch(loginJob) {
            val waitScan = launch {
                val (url, oauthKey) = defaultCookiesClient.applyForLoginQrcode()
                withContext(Dispatchers.JavaFx) {
                    loginModel.qrcodeUrl = url
                }
                while (true) {
                    val (success, data) = defaultCookiesClient.loginInfo(oauthKey)
                    delay(2000)
                    LOG.debug { "success: $success , data : $data" }
                    if (success) {
                        defaultCookiesClient.get<Unit>(data as String)
                        break
                    }
                }
            }
            waitScan.join()
            // login success
            launch(loginJob) {
                val info = defaultCookiesClient.accountInfo()
                withContext(Dispatchers.JavaFx) {
                    loginModel.uname = info.uname
                    loginModel.login = true
                    loginModel.qrcodeUrl = ""
                }
            }
        }
    }

    fun logout(loginModel: LoginModel) {
        loginJob.cancelChildren()
        launch {
            withContext(Dispatchers.JavaFx) {
                loginModel.uname = ""
                loginModel.login = false
                loginModel.qrcodeUrl = ""
            }
        }
    }

    fun stop() {
        backend.stopAll()
    }
}
