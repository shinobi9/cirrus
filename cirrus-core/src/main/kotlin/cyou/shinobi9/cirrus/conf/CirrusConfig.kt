package cyou.shinobi9.cirrus.conf

data class CirrusConfig(
    var threadsCount: Int = 10,
    var useDispatchersIO: Boolean = false,
    var reconnect: Boolean = true,
)
