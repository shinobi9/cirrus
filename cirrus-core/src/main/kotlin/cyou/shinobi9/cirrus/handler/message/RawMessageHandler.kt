package cyou.shinobi9.cirrus.handler.message

interface RawMessageHandler : MessageHandler {
    fun onMessage(block: (message: String) -> Unit)
    fun onHeartBeat(block: (number: Int) -> Unit)
}

class RawMessageHandlerImpl(
    private var onMsg: ((message: String) -> Unit)? = null,
    private var onHeartBeat: ((number: Int) -> Unit)? = null,
) : RawMessageHandler {

    override fun onMessage(block: (message: String) -> Unit) {
        onMsg = block
    }

    override fun onHeartBeat(block: (number: Int) -> Unit) {
        onHeartBeat = block
    }

    override fun handle(message: String) {
        onMsg?.invoke(message)
    }

    override fun handleHeartBeat(number: Int) {
        onHeartBeat?.invoke(number)
    }
}

inline fun rawMessageHandler(content: RawMessageHandler.() -> Unit) = RawMessageHandlerImpl().apply(content)
