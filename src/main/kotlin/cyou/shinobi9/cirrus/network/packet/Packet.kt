package cyou.shinobi9.cirrus.network.packet

import java.nio.ByteBuffer

class Packet private constructor(header: PacketHead, payload: ByteBuffer) {
    var header: PacketHead = header
        private set
    var payload: ByteBuffer = payload
        private set

    companion object {
        fun createPacket(header: PacketHead, payload: ByteBuffer): Packet {
            return Packet(header, payload).apply { calcHeaderLength() }
        }

        fun createPacket(
            header: PacketHead,
            payload: ByteBuffer,
            packLength: Int,
            headLength: Short
        ): Packet {
            return Packet(header, payload).apply {
                with(this.header) {
                    this.packLength = packLength
                    this.headLength = headLength
                }
            }
        }

        fun resolve(buffer: ByteBuffer): Packet {
            with(buffer) {
                val packLength = int
                val headLength = short
                val version = short
                val code = int
                val seq = int
                val body = ByteArray(buffer.remaining())
                get(body)
                return createPacket(
                    PacketHead(
                        searchVersion(version, true),
                        searchOperation(code, true),
                        seq
                    ),
                    ByteBuffer.wrap(body), packLength, headLength
                )
            }
        }
    }

    fun toByteBuffer(): ByteBuffer {
        return ByteBuffer.allocate(header.packLength).apply {
            putInt(header.packLength)
            putShort(header.headLength)
            putShort(header.version.version)
            putInt(header.code.code)
            putInt(header.seq)
            put(payload)
            flip()
        }
    }

    private fun calcHeaderLength() {
        header.packLength = header.headLength + payload.limit()
    }
}
