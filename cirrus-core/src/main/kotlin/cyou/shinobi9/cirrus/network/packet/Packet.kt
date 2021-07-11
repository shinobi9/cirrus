package cyou.shinobi9.cirrus.network.packet

import java.nio.ByteBuffer

class Packet private constructor(packetMask: PacketMask, packetPayload: ByteBuffer) {
    var mask: PacketMask = packetMask
        private set
    var payload: ByteBuffer = packetPayload
        private set

    companion object {
        fun createPacket(packetMask: PacketMask, packetPayload: ByteBuffer): Packet {
            return Packet(packetMask, packetPayload).apply { calcMaskLength() }
        }

        fun createPacket(
            _mask: PacketMask,
            _payload: ByteBuffer,
            _packLength: Int,
            _maskLength: Short
        ): Packet {
            return Packet(_mask, _payload).apply {
                with(mask) {
                    packLength = _packLength
                    maskLength = _maskLength
                }
            }
        }

        fun resolve(buffer: ByteBuffer): Packet {
            with(buffer) {
                val packLength = int
                val maskLength = short
                val version = short
                val code = int
                val seq = int
                val body = ByteArray(buffer.remaining())
                get(body)
                return createPacket(
                    PacketMask(
                        searchVersion(version, true),
                        searchOperation(code, true),
                        seq
                    ),
                    ByteBuffer.wrap(body), packLength, maskLength
                )
            }
        }
    }

    fun toByteBuffer(): ByteBuffer {
        return ByteBuffer.allocate(mask.packLength).apply {
            putInt(mask.packLength)
            putShort(mask.maskLength)
            putShort(mask.version.version)
            putInt(mask.code.code)
            putInt(mask.seq)
            put(payload)
            flip()
        }
    }

    private fun calcMaskLength() {
        mask.packLength = mask.maskLength + payload.limit()
    }
}
