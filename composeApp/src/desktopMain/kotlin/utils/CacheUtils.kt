package utils

import java.io.File
import java.io.IOException

object CacheUtils {
    val cacheDir = File(PathUtils.getConfigPath("cache"))

    fun readString(filename: String): String {
        runCatching {
            return buildCacheFile(filename).readText()
        }.onFailure {
            it.printStackTrace()
        }

        return "{}"
    }

    fun writeString(filename: String, text: String): Boolean {
        runCatching {
            buildCacheFile(filename).writeText(text)
            return true
        }.onFailure {
            it.printStackTrace()
        }

        return false
    }

    @Throws(IOException::class)
    private fun buildCacheFile(filename: String): File {
        val file = File(cacheDir, filename)
        val fileParent = file.parentFile ?: throw IOException("not found: ${file.parentFile}")

        if (!fileParent.exists()) {
            fileParent.mkdirs()
        }

        if (!file.exists()) {
            file.createNewFile()
        }

        return file.also { LogUtils.debug("cache file: ${it.absolutePath}") }
    }
}