package cyou.shinobi9.cirrus.handler.event

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.handler.event.EventType.*

interface SimpleEventHandler : EventHandler {
    fun onConnect(block: (Cirrus) -> Unit)
    fun onConnected(block: (Cirrus) -> Unit)
    fun onDisconnect(block: (Cirrus) -> Unit)
    fun onLogin(block: (Cirrus) -> Unit)
    fun onLoginSuccess(block: (Cirrus) -> Unit)
    fun onLoginFail(block: (Cirrus) -> Unit)
}

class SimpleEventHandlerImpl : SimpleEventHandler {
    private var connect: ((Cirrus) -> Unit)? = null
    private var connected: ((Cirrus) -> Unit)? = null
    private var disconnect: ((Cirrus) -> Unit)? = null
    private var loginFail: ((Cirrus) -> Unit)? = null
    private var login: ((Cirrus) -> Unit)? = null
    private var loginSuccess: ((Cirrus) -> Unit)? = null
    override fun onConnect(block: (Cirrus) -> Unit) {
        connect = block
    }

    override fun onConnected(block: (Cirrus) -> Unit) {
        connected = block
    }

    override fun onDisconnect(block: (Cirrus) -> Unit) {
        disconnect = block
    }

    override fun onLogin(block: (Cirrus) -> Unit) {
        login = block
    }

    override fun onLoginSuccess(block: (Cirrus) -> Unit) {
        loginSuccess = block
    }

    override fun onLoginFail(block: (Cirrus) -> Unit) {
        loginFail = block
    }

    override fun handle(eventType: EventType, cirrus: Cirrus) {
        when (eventType) {
            CONNECTED -> connected?.invoke(cirrus)
            CONNECT -> connect?.invoke(cirrus)
            LOGIN -> login?.invoke(cirrus)
            LOGIN_SUCCESS -> loginSuccess?.invoke(cirrus)
            LOGIN_FAILED -> loginFail?.invoke(cirrus)
            DISCONNECT -> disconnect?.invoke(cirrus)
            else -> {
            }
        }
    }
}

inline fun simpleEventHandler(content: SimpleEventHandler.() -> Unit) = SimpleEventHandlerImpl().apply(content)
