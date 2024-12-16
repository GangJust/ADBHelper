package adb.entity

import kotlinx.serialization.Serializable

@Serializable
data class AppDesc(
    // 包名
    val packageName: String,
    // 启动活动
    val launcherActivity: String,
    // 主CPU架构
    val primaryCpuAbi: String,
    // 版本名
    val versionName: String,
    // 版本号
    val versionCode: String,
    // 最小SDK版本
    val minSdk: String,
    // 目标SDK版本
    val targetSdk: String,
    // 时间戳
    val timeStamp: String,
    // 安装时间
    val firstInstallTime: String,
    // 更新时间
    val lastUpdateTime: String,
    // 签名版本
    val signVersion: String,
    // 数据目录
    val dataDir: String,
    // 外部数据目录
    val externalDataDir: String,
    // 安装路径
    val installPath: String,
    // 应用大小
    val size: String,
    // 是否系统应用
    val isSystem: Boolean,
) {

    // Gson 需要保留一个空参构造方法
    // Gson Need to retain an empty parameter construction method
    constructor() : this(
        packageName = "",
        launcherActivity = "",
        primaryCpuAbi = "",
        versionName = "",
        versionCode = "",
        minSdk = "",
        targetSdk = "",
        timeStamp = "",
        firstInstallTime = "",
        lastUpdateTime = "",
        signVersion = "",
        dataDir = "",
        externalDataDir = "",
        installPath = "",
        size = "",
        isSystem = false,
    )

    companion object {
        @JvmField
        val EMPTY = AppDesc(
            packageName = "",
            launcherActivity = "",
            primaryCpuAbi = "",
            versionName = "",
            versionCode = "",
            minSdk = "",
            targetSdk = "",
            timeStamp = "",
            firstInstallTime = "",
            lastUpdateTime = "",
            signVersion = "",
            dataDir = "",
            externalDataDir = "",
            installPath = "",
            size = "",
            isSystem = false,
        )
    }
}
