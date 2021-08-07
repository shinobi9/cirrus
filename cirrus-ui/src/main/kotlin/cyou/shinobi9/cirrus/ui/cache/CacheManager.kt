package cyou.shinobi9.cirrus.ui.cache

import cyou.shinobi9.cirrus.network.userAvatar
import cyou.shinobi9.cirrus.ui.LOG
import cyou.shinobi9.cirrus.ui.defaultCookiesClient
import io.ktor.http.*
import kotlinx.coroutines.*
import java.io.File
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
    private suspend fun resolveInDisk(dir: String, key: String): Pair<Boolean, String?> =
        withContext(Dispatchers.IO) {
            val path = Path("""${root.absolutePathString()}${File.separator}$dir""")
            val files = path.toFile().listFiles()
            val file = files?.singleOrNull { it.name.split(".")[0] == key }
            return@withContext if (file != null) {
                LOG.debug { "resolve from ${file.absolutePath}" }
                true to file.absolutePath
            } else false to null
        }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun resolveByHttp(id: Int): Triple<Boolean, String?, String?> = withContext(Dispatchers.IO) {
        val imageUrl = defaultCookiesClient.userAvatar(id)
        val mini = "$imageUrl@60w_60h_1o.webp"
        LOG.debug { mini }
        val ext = "webp"
        val name = """$id.$ext"""
        return@withContext Triple(true, mini, name)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun save(dir: String, key: String, url: String): Path = withContext(Dispatchers.IO) {
        val path = Path("""${root.absolutePathString()}${File.separator}$dir${File.separator}$key""")
        LOG.debug { "save to ${path.absolutePathString()}" }
        Files.copy(URL(url).openStream(), path)
        return@withContext path
    }

    fun resolveAvatar(id: Int): String? {
        return runBlocking { withContext(Dispatchers.IO) { doResolveAvatar(id) } }
    }

    private suspend fun doResolveAvatar(id: Int): String? {
        checkExist()
        checkAvatarExist()
        val (onDisk, inputStream) = resolveInDisk(AVATAR_DIR, id.toString())
        if (onDisk) {
            return inputStream!!
        }
        val (result, url, name) = resolveByHttp(id)
        if (result) {
            val path = save(AVATAR_DIR, name!!, url!!)
            println(path.toUri().toString())
            return path.toUri().toString()
        }
        return null
    }

    fun clearCache() {
        launch {
            withContext(Dispatchers.IO) {
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
