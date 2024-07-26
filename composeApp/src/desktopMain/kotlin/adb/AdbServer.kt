package adb

import adb.entity.*
import compose.BuildConfig
import utils.OsUtils
import utils.PathUtils
import java.io.File

class AdbServer private constructor() {
    companion object {
        init {
            if (BuildConfig.DEBUG) {
                loadLibrary("../api/adb_helper_api_rs/target/debug", "api_jni")
            } else {
                loadLibrary("api_jni")
            }
        }

        private fun loadLibrary(name: String) {
            loadLibrary(PathUtils.getComposeLibsPath(), name)
        }

        private fun loadLibrary(path: String, name: String) {
            val libFile = if (OsUtils.isMac) {
                File(path, "lib${name}.dylib")
            } else if (OsUtils.isWindows) {
                File(path, "${name}.dll")
            } else if (OsUtils.isLinux) {
                File(path, "lib${name}.so")
            } else {
                throw Exception("unsupported operating systems.")
            }

            System.load(libFile.absolutePath)
        }

        private var _instance: AdbServer? = null

        fun initialize(workerPath: String) {
            _instance ?: synchronized(this) {
                _instance ?: AdbServer().apply {
                    setWorkDir(workerPath)
                    _instance = this
                }
            }
        }

        val instance: AdbServer
            get() = _instance ?: throw Exception("AdbServer is not initialization.")

        fun test(workerPath: String): String {
            _instance ?: synchronized(this) {
                _instance ?: AdbServer().apply {
                    _instance = this
                }
            }

            _instance?.setWorkDir(workerPath)
            val result = _instance?.startServer() ?: "test failed."
            _instance?.killServer()
            return result
        }
    }

    /**
     * 设置adb工作目录|Set adb working directory
     */
    private external fun setWorkDir(path: String): String

    /**
     * 启动adb服务|Start adb service
     */
    external fun startServer(): String

    /**
     * 关闭adb服务|Close adb service
     */
    external fun killServer(): String

    /**
     * adb服务是否在应用区间被杀死
     */
    external fun isKilled(): Boolean

    /**
     * 获取adb版本|Get adb version
     */
    external fun version(): Version

    /**
     * 通过局域网IP连接设备|Connect devices through LAN IP
     *
     * @param ip 设备IP，支持端口号，如:192.168.1.111:5555|Device IP, supports port numbers, such as 192.168.1.1111:5555
     */
    external fun connect(ip: String): String

    /**
     * 获取设备列表|Get device list
     */
    external fun getDevices(): List<Device>

    /**
     * 获取前台应用基本活动信息|Get the basic activity information of the frontend application
     *
     * @param serialNo 设备序列号|Device serial number
     */
    external fun getActivity(serialNo: String): Activity

    /**
     * 获取设备安装的应用包名列表|Get the list of app package names installed on the device
     *
     * @param serialNo 设备序列号|Device serial number
     * @param isSystem 是否只系统应用|Is it a system application only
     */
    external fun getPackages(serialNo: String, isSystem: Boolean = false): List<String>

    /**
     * 获取应用基本信息|Obtain basic application information
     *
     * @param serialNo 设备序列号|Device serial number
     * @param packageName 应用包名|Package name
     */
    external fun getAppDesc(serialNo: String, packageName: String): AppDesc

    /**
     * 获取文件描述列表|Get file desc list
     *
     * @param serialNo 设备序列号|Device serial number
     * @param path 文件路径|dir path
     */
    external fun getFiles(serialNo: String, path: String): List<FileDesc>

    /**
     * 获取文件类型|Get file kind
     *
     * @param serialNo 设备序列号|Device serial number
     * @param path 文件路径|dir path
     */
    external fun getFileKind(serialNo: String, path: String): String

    /**
     * 从设备拉取文件到本机|Pull files from the device to the local machine
     *
     * @param serialNo 设备序列号|Device serial number
     * @param remotePath 设备文件路径|Device file path
     * @param localPath 本地文件路径|Local file path
     */
    external fun pullFile(serialNo: String, remotePath: String, localPath: String): String

    /**
     * 从本机推送文件到设备|Push files from the local machine to the device
     *
     * @param serialNo 设备序列号|Device serial number
     * @param localPath 本地文件路径|Local file path
     * @param remotePath 设备文件路径|Device file path
     */
    external fun pushFile(serialNo: String, localPath: String, remotePath: String): String

    /**
     * 安装apk|install apk
     *
     * @param serialNo 设备序列号|Device serial number
     * @param apkPath apk文件路径|apk path
     */
    external fun installApk(serialNo: String, apkPath: String): String

    /**
     * 卸载apk|uninstall
     *
     * @param serialNo 设备序列号|Device serial number
     * @param packageName 应用包名|package name
     */
    external fun uninstallApk(serialNo: String, packageName: String): String

    /**
     * 截图，并返回数据|Take a screenshot and return the data
     *
     * @param serialNo 设备序列号|Device serial number
     */
    external fun screenshot(serialNo: String): Screenshot

    /**
     * 获取布局|Get Layout
     *
     * @param serialNo 设备序列号|Device serial number
     */
    external fun getLayout(serialNo: String): String

    /**
     * 同步执行shell命令。
     * 不要调用该方法执行阻塞终端的命令, 如: logcat, 这将无任何输出并会造成线程阻塞。
     *
     * Synchronize execution of shell commands.
     * Do not call this method to execute terminal-blocking commands, such as logcat, which will produce no output and cause the thread to block.
     *
     * @param serialNo 设备序列号|Device serial number
     * @param cmd shell命令|shell cmd
     * @param su 是否使用root权限执行|Whether to execute with root privileges
     */
    external fun shellSyn(serialNo: String, cmd: String, su: Boolean = false): String


    /// 包装方法|wrapper
    fun getActivity(device: Device): Activity {
        return getActivity(device.serialNo)
    }

    fun getPackages(device: Device, isSystem: Boolean = false): List<String> {
        return getPackages(device.serialNo, isSystem)
    }

    fun getAppDesc(device: Device, packageName: String): AppDesc {
        return getAppDesc(device.serialNo, packageName)
    }

    fun getFiles(device: Device, path: String): List<FileDesc> {
        return getFiles(device.serialNo, path)
    }

    fun getFileKind(device: Device, path: String): String {
        return getFileKind(device.serialNo, path)
    }

    fun getFileKind(device: Device, desc: FileDesc): String {
        return if (desc.isLink) {
            getFileKind(device.serialNo, "${desc.path}${desc.name.split(" ").first()}")
        } else {
            getFileKind(device.serialNo, "${desc.path}${desc.name}")
        }
    }

    fun pullFile(device: Device, remotePath: String, localPath: String): String {
        return pullFile(device.serialNo, remotePath, localPath)
    }

    fun pushFile(device: Device, localPath: String, remotePath: String): String {
        return pushFile(device.serialNo, localPath, remotePath)
    }

    fun installApk(device: Device, apkPath: String): String {
        return installApk(device.serialNo, apkPath)
    }

    fun uninstallApk(device: Device, packageName: String): String {
        return uninstallApk(device.serialNo, packageName)
    }

    fun screenshot(device: Device): Screenshot {
        return screenshot(device.serialNo)
    }

    fun getLayout(device: Device): String {
        return getLayout(device.serialNo)
    }

    fun shellSyn(device: Device, cmd: String, su: Boolean = false): String {
        return shellSyn(device.serialNo, cmd, su)
    }
}