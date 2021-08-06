package cyou.shinobi9.cirrus.network

import kotlinx.serialization.Serializable

@Serializable
data class SimpleWrap<T>(
    val data: T,
)
