package cyou.shinobi9.cirrus.conf

data class CirrusConfig(
    val threadsCount: Int = 10,
    val useDispatchersIO: Boolean = false,
)
