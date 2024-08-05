package utils

import compose.BuildConfig
import java.text.SimpleDateFormat
import java.util.Calendar

object LogUtils {
    enum class Level(val value: String) {
        INFO("I"),
        DEBUG("D"),
    }

    private val formatLocal = object : ThreadLocal<MutableMap<String, SimpleDateFormat>>() {
        override fun initialValue(): MutableMap<String, SimpleDateFormat> {
            return mutableMapOf()
        }
    }

    fun log(level: Level, msg: String) {
        val parent = "yyyy-MM-dd HH:mm:ss"
        val format = formatLocal.get().getOrPut(parent) { SimpleDateFormat(parent) }
        println("[${level.value}/${format.format(Calendar.getInstance().time)}]: $msg")
    }

    fun info(msg: String) {
        log(Level.INFO, msg)
    }

    fun debug(msg: String) {
        if (BuildConfig.DEBUG)
            log(Level.DEBUG, msg)
    }
}