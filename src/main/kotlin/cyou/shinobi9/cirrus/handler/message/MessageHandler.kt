package cyou.shinobi9.cirrus.handler.message

@MessageHandlerDsl
interface MessageHandler {
    fun handle(message: String)
}
