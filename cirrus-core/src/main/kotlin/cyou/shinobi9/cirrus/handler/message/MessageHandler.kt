package cyou.shinobi9.cirrus.handler.message

@MessageHandlerDsl
interface MessageHandler {
    fun handle(message: String)
    fun handleHeartBeat(number: Int)
}
