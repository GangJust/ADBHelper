package utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

object CacheUtils {
    val gson: Gson by lazy { Gson() }

    fun readCacheString(filename: String): String {
        runCatching {
            val file = File(PathUtils.getConfigPath("cache"), filename)
            if (!file.exists()) {
                file.createNewFile()
                file.writeText("{}")
            }

            return file.readText()
        }.onFailure {
            it.printStackTrace()
        }

        return ""
    }

    fun writeCacheString(filename: String, text: String): Boolean {
        runCatching {
            val file = File(PathUtils.getConfigPath("cache"), filename)
            if (!file.exists()) {
                file.createNewFile()
            }

            file.writeText(text)

            return true
        }.onFailure {
            it.printStackTrace()
        }

        return false
    }

    fun readCacheJsonObject(filename: String): JsonObject {
        runCatching {
            val file = File(PathUtils.getConfigPath("cache"), filename)
            if (!file.exists()) {
                file.createNewFile()
                file.writeText("{}")
            }

            val readText = file.readText()
            return gson.fromJson(readText, JsonObject::class.java)
        }.onFailure {
            it.printStackTrace()
        }

        return JsonObject()
    }

    fun writeCacheJsonJsonObject(filename: String, json: JsonObject): Boolean {
        runCatching {
            val file = File(PathUtils.getConfigPath("cache"), filename)
            if (!file.exists()) {
                file.createNewFile()
            }

            file.writeText(gson.toJson(json))

            return true
        }.onFailure {
            it.printStackTrace()
        }

        return false
    }
}