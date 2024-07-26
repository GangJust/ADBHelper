package entity

import utils.ConfigUtils

class AppConfig private constructor(
    val adbPath: String,
    val scrcpyPath: String,
) {
    companion object {
        private const val ADB_PATH = "adb.path"
        private const val SCRCPY_PATH = "scrcpy.path"


        fun read(): AppConfig {
            val config = ConfigUtils.read()
            return AppConfig(
                adbPath = config.getProperty(ADB_PATH, ""),
                scrcpyPath = config.getProperty(SCRCPY_PATH, ""),
            )
        }

        fun write(
            adbPath: String? = null,
            scrcpyPath: String? = null,
        ) {
            val config = ConfigUtils.read()
            adbPath?.also { config.setProperty(ADB_PATH, it) }
            scrcpyPath?.also { config.setProperty(SCRCPY_PATH, it) }
            ConfigUtils.write(config)
        }
    }
}