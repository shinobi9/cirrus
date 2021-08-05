package cyou.shinobi9.cirrus.ui.cache

import cyou.shinobi9.cirrus.network.userAvatar
import cyou.shinobi9.cirrus.ui.LOG
import cyou.shinobi9.cirrus.ui.defaultClient
import kotlinx.coroutines.*
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.*

class CacheManager : CoroutineScope {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LOG.error(throwable) { "缓存读取错误" }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + exceptionHandler

    private val root = createTempDirectory(PREFIX).also {
        it.toFile().deleteOnExit()
    }

    private val avatar = Path("""${root.absolutePathString()}${File.separator}$AVATAR_DIR""")

    companion object {
        const val AVATAR_DIR = "avatar"
        const val PREFIX = "cirrus-"
    }

    init {
        LOG.info { "temp dir for cache is ${root.absolutePathString()}" }
    }

    private fun checkExist() {
        if (!root.exists()) root.createDirectories()
    }
    private fun checkAvatarExist() {
        if (!avatar.exists()) avatar.createDirectories()
    }
    @Suppress("SameParameterValue")
    private fun resolveInDisk(dir: String, key: String): Pair<Boolean, InputStream?> {
        val path = Path("""${root.absolutePathString()}${File.separator}$dir""")
        val files = path.toFile().listFiles()
        val file = files?.singleOrNull { it.name.split(".")[0] == key }
        return if (file != null) {
            LOG.debug { "resolve from ${file.absolutePath}" }
            true to file.inputStream()
        } else false to null
    }

    private fun resolveByHttp(id: Int): Triple<Boolean, InputStream?, String?> {
        return runBlocking {
            val imageUrl = defaultClient.userAvatar(id)
            LOG.debug { imageUrl }
            val url = URL(imageUrl)
            val ext = url.path.replace("/", "").split(".")[1]
            val name = """$id.$ext"""
            Triple(true, url.openStream(), name)
        }
    }

    @Suppress("SameParameterValue")
    private fun save(dir: String, key: String, inputStream: InputStream): Path {
        val path = Path("""${root.absolutePathString()}${File.separator}$dir${File.separator}$key""")
        LOG.debug { "save to ${path.absolutePathString()}" }
        Files.copy(inputStream, path)
        return path
    }

    fun resolveAvatar(id: Int): InputStream? {
        checkExist()
        checkAvatarExist()
        val (onDisk, inputStream) = resolveInDisk(AVATAR_DIR, id.toString())
        if (onDisk) {
            return inputStream!!
        }
        val (result, stream, name) = resolveByHttp(id)
        if (result) {
            val path = save(AVATAR_DIR, name!!, stream!!)
            return path.inputStream()
        }
        return null
    }

    fun clearCache() = launch {
        withContext(currentCoroutineContext()) {
            val files = root.parent.toFile().listFiles()
            val tmpDirectories = files?.filter { it.isDirectory && it.name.startsWith(PREFIX) }.orEmpty()
            LOG.debug { "delete $tmpDirectories" }

            tmpDirectories.forEach {
                LOG.debug { "delete ${it.name}" }
                it.recursiveDelete()
                it.delete()
            }
        }
    }

    private fun File.recursiveDelete() {
        listFiles()?.forEach {
            if (it.isDirectory && it.listFiles().orEmpty().isNotEmpty()) {
                it.recursiveDelete()
            } else {
                LOG.debug { "delete ${it.name}" }
                it.delete()
            }
        }
    }
}
