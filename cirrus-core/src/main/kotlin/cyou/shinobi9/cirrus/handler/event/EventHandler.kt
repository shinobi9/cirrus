package cyou.shinobi9.cirrus.handler.event

import cyou.shinobi9.cirrus.Cirrus

interface EventHandler {
    fun handle(eventType: EventType, cirrus: Cirrus)
}
