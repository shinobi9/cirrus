package cyou.shinobi9.cirrus.handler.message

interface RawMessageHandler : MessageHandler {
    fun onMessage(block: (message: String) -> Unit)
}

class RawMessageHandlerImpl(
    private var onMsg: ((message: String) -> Unit)? = null
) : RawMessageHandler {

    override fun onMessage(block: (message: String) -> Unit) {
        onMsg = block
    }

    override fun handle(message: String) {
        onMsg?.invoke(message)
    }
}

inline fun rawMessageHandler(content: RawMessageHandler.() -> Unit) = RawMessageHandlerImpl().apply(content)
