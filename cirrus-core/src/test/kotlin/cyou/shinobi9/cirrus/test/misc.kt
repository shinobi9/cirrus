package cyou.shinobi9.cirrus.test

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.handler.message.simpleMessageHandler
import mu.KotlinLogging

val TEST_LOG = KotlinLogging.logger {}

val TEST_MESSAGE_HANDLER = simpleMessageHandler {
    onReceiveDanmaku { user, said ->
        TEST_LOG.info { "$user : $said" }
    }
    onUserEnterInLiveRoom {
        TEST_LOG.info { "$it enter in" }
    }
}

val TEST_CIRRUS = Cirrus().apply { messageHandler = TEST_MESSAGE_HANDLER }
