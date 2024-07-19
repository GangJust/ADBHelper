package adb.entity

data class Activity(
    // 包名
    val packageName: String,
    // 进程名
    val processName: String,
    // 启动活动
    val launchActivity: String,
    // 当前活动
    val resumedActivity: String,
    // 上次活动
    val lastActivity: String,
    // 活动堆栈
    val stackActivities: List<String>,
) {

    // Gson 需要保留一个空参构造方法
    // Gson Need to retain an empty parameter construction method
    constructor() : this(
        packageName = "",
        processName = "",
        lastActivity = "",
        resumedActivity = "",
        launchActivity = "",
        stackActivities = emptyList(),
    )

    fun fullLaunchActivity(bool: Boolean = false): String {
        return launchActivity.fullClassName(bool)
    }

    fun fullResumedActivity(bool: Boolean = false): String {
        return resumedActivity.fullClassName(bool)
    }

    fun fullLastActivity(bool: Boolean = false): String {
        return lastActivity.fullClassName(bool)
    }

    fun fullStackActivities(bool: Boolean = false): List<String> {
        return stackActivities.map { it.fullClassName(bool) }
    }

    private fun String.fullClassName(bool: Boolean): String {
        if (!bool)
            return this

        if (this.isBlank())
            return this

        val indexOf = this.indexOf("/")
        if (indexOf == -1)
            return this

        val packageName = this.substring(0, indexOf)
        val className = this.substring(indexOf + 1)
        if (className.indexOf(".") == 0) {
            return "${packageName}$className"
        }

        return className
    }

    companion object {
        @JvmField
        val EMPTY = Activity(
            packageName = "",
            processName = "",
            lastActivity = "",
            resumedActivity = "",
            launchActivity = "",
            stackActivities = emptyList(),
        )
    }
}