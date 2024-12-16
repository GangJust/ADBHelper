package adb.entity

import kotlinx.serialization.Serializable

@Serializable
data class Version(
    // 版本名称
    val versionName: String,
    // 版本号
    val versionCode: String,
    // 安装路径
    val installPath: String,
    // 操作系统
    val runningOs: String
) {

    // Gson 需要保留一个空参构造方法
    // Gson Need to retain an empty parameter construction method
    constructor() : this(
        versionName = "",
        versionCode = "",
        installPath = "",
        runningOs = "",
    )

    companion object {
        @JvmField
        val EMPTY: Version = Version(
            versionName = "",
            versionCode = "",
            installPath = "",
            runningOs = "",
        )
    }
}
