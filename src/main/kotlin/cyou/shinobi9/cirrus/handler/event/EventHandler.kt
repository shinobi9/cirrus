package cyou.shinobi9.cirrus.handler.event

interface EventHandler {
    fun handle(eventType: EventType)
}
