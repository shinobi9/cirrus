package cyou.shinobi9.cirrus.network.packet

import cyou.shinobi9.cirrus.network.packet.CMD.Companion.byCommand
import cyou.shinobi9.cirrus.network.packet.Operation.Companion.byCode
import cyou.shinobi9.cirrus.network.packet.Version.Companion.byVersion

fun searchCMD(cmd: String, throwIfNotFound: Boolean = false) = byCommand[cmd]
    ?: if (throwIfNotFound) throw UnknownProtocolTypeException("unknown cmd : $cmd") else CMD.UNKNOWN

fun searchOperation(operation: Int, throwIfNotFound: Boolean = false) = byCode[operation]
    ?: if (throwIfNotFound) throw UnknownProtocolTypeException("unknown operation : $operation") else Operation.UNKNOWN

fun searchVersion(version: Short, throwIfNotFound: Boolean = false) = byVersion[version]
    ?: if (throwIfNotFound) throw UnknownProtocolTypeException("unknown version : $version") else Version.UNKNOWN

class UnknownProtocolTypeException(message: String) : RuntimeException(message)
