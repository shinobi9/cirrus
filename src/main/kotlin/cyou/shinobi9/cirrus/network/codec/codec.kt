package cyou.shinobi9.cirrus.network.codec

import cyou.shinobi9.cirrus.LOG
import cyou.shinobi9.cirrus.handler.event.EventHandler
import cyou.shinobi9.cirrus.handler.message.MessageHandler
import cyou.shinobi9.cirrus.network.packet.Operation.*
import cyou.shinobi9.cirrus.network.packet.Packet
import cyou.shinobi9.cirrus.network.packet.Version
import cyou.shinobi9.cirrus.network.packet.searchOperation
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.zip.InflaterOutputStream
import kotlin.text.Charsets.UTF_8

fun uncompressZlib(input: ByteArray): ByteArray =
    ByteArrayOutputStream().use { InflaterOutputStream(it).use { output -> output.write(input) }; return@use it.toByteArray() }

fun decode(
    buffer: ByteBuffer,
    messageHandler: MessageHandler?,
    eventHandler: EventHandler?

) {
    val packet = Packet.resolve(buffer)
    val header = packet.header
    val payload = packet.payload
    when (header.code) {
        HEARTBEAT_REPLY -> LOG.debug { "receive heart beat packet" }
        AUTH_REPLY -> {
            val message = payload.array().toString(UTF_8)
            LOG.info { "auth info response => $message" }
        }
        SEND_MSG_REPLY -> {
            if (header.version == Version.WS_BODY_PROTOCOL_VERSION_DEFLATE) {
                decode(ByteBuffer.wrap(uncompressZlib(payload.array())), messageHandler, eventHandler)
                return
            }
            require(header.version == Version.WS_BODY_PROTOCOL_VERSION_NORMAL)
            val byteArray = ByteArray(header.packLength - header.headLength)
            payload.get(byteArray)
            byteArray.toString(UTF_8).let { message ->
                LOG.debug { message }
                messageHandler?.handle(message)
            }
            if (payload.hasRemaining())
                decode(payload, messageHandler, eventHandler)
        }
        else -> {
            val operation = searchOperation(header.code.code)
            if (operation == UNKNOWN)
                LOG.warn { "code unknown! => ${header.code.code}" }
            else
                LOG.warn { "code exist in dictionary , but now haven't been handle => name : ${operation.name} , code : ${operation.code} " }
        }
    }
}
