package cyou.shinobi9.cirrus.network.packet

enum class Version(val version: Short) {
    WS_BODY_PROTOCOL_VERSION_NORMAL(0),
    WS_BODY_PROTOCOL_VERSION_INT(1), // 用于心跳包
    WS_BODY_PROTOCOL_VERSION_DEFLATE(2),
    UNKNOWN(Short.MIN_VALUE);

    companion object {
        val byVersion = values().associateBy { it.version }
    }
}
