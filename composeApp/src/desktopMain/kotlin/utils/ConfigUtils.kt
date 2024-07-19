package utils

import java.io.File
import java.util.Properties

object ConfigUtils {
    private val file = File(PathUtils.getConfigPath(), "config.properties")

    fun read(): Properties {
        val properties = Properties()
        if (file.exists()) {
            properties.load(file.inputStream())
        }
        return properties
    }

    fun write(properties: Properties) {
        properties.store(file.outputStream(), "config")
    }
}