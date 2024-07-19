package utils

import java.util.*

object OsUtils {

    fun getOsName(): String {
        return System.getProperty("os.name").lowercase(Locale.getDefault())
    }

    val isMac: Boolean
        get() = getOsName().contains("mac")

    val isWindows: Boolean
        get() = getOsName().contains("win")

    val isLinux: Boolean
        get() = getOsName().contains("nix") || getOsName().contains("nux") || getOsName().contains("aix")
}