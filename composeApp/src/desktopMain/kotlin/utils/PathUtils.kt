package utils

import java.awt.Desktop
import java.io.File

object PathUtils {

    /**
     * 打开文件夹，使用系统默认文件管理器
     * Open the folder and use the system's default file manager
     *
     * @param path 文件夹路径
     */
    fun openDir(path: String?): Boolean {
        path ?: return false

        return runCatching {
            val desktop = Desktop.getDesktop()
            val file = File(path)
            desktop.open(file)
        }.onFailure {
            it.printStackTrace()
        }.isSuccess
    }

    /**
     * 获取下载路径
     * Get download path
     */
    fun getDownloadPath(): String? {
        return if (OsUtils.isWindows) {
            System.getenv("USERPROFILE") + "\\Downloads"
        } else if (OsUtils.isMac) {
            System.getProperty("user.home") + "/Downloads"
        } else if (OsUtils.isLinux) {
            System.getProperty("user.home") + "/Downloads"
        } else {
            null
        }
    }

    /**
     * 获取用户主目录
     * Get use home path
     */
    fun getUserHomePath(): String {
        return System.getProperty("user.home")
            ?: throw Exception("user.home is null")
    }

    /**
     * 获取Compose资源路径, Compose内置路径
     * Get compose resource path, this is provided internally by compose.
     */
    fun getComposeResourcePath(): String {
        return System.getProperty("compose.application.resources.dir")
            ?: throw Exception("compose.application.resources.dir is null")
    }

    /**
     * 获取Compose库路径, `gradleTask :copyApiRelease` 自定义路径
     * Get the path to the Compose library, `gradleTask :copyApiRelease` Custom Paths
     */
    fun getComposeLibsPath(): String {
        return getComposeResourcePath().let {
            File(File(it).parentFile, "libs").absolutePath
        }
    }

    /**
     * 获取Skiko库路径，Compose内置路径`create`打包后才会有该属性
     * Get Skiko library path, Compose built-in path `create` will only have this attribute after packaging
     */
    fun getSkikoLibraryPath(): String {
        return System.getProperty("skiko.library.path")
            ?: throw Exception("skiko.library.path is null")
    }

    /**
     * 获取配置路径
     * Get Configuration Path
     */
    fun getConfigPath(path: String? = null): String {
        // 创建配置目录
        val config = File(getUserHomePath(), ".adb-helper")
        if (!config.exists())
            config.mkdirs()

        if (path == null)
            return config.absolutePath

        // 创建子目录
        val configChild = File(config, path)
        if (!configChild.exists())
            configChild.mkdirs()

        return configChild.absolutePath
    }
}