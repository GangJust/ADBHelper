package utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.File
import java.io.IOException

object CacheUtils {
    val gson: Gson by lazy { Gson() }

    val cacheDir = File(PathUtils.getConfigPath("cache"))

    fun readString(filename: String): String {
        runCatching {
            return buildCacheFile(filename).readText()
        }.onFailure {
            it.printStackTrace()
        }

        return ""
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

    fun readJsonObj(filename: String): JsonObject {
        runCatching {
            val readText = buildCacheFile(filename).readText()
            if (readText.isEmpty())
                return JsonObject()

            return gson.fromJson(readText, JsonObject::class.java)
        }.onFailure {
            it.printStackTrace()
        }

        return JsonObject()
    }

    fun readJsonArr(filename: String): JsonArray {
        runCatching {
            val readText = buildCacheFile(filename).readText()
            if (readText.isEmpty())
                return JsonArray()

            return gson.fromJson(readText, JsonArray::class.java)
        }.onFailure {
            it.printStackTrace()
        }

        return JsonArray()
    }

    fun writeJson(filename: String, json: JsonElement): Boolean {
        runCatching {
            buildCacheFile(filename).writeText(gson.toJson(json))
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