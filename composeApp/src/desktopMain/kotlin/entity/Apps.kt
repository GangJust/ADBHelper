package entity

import adb.entity.AppDesc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Apps(
    @SerialName("system_apps")
    val systemApps: List<AppDesc>,
    @SerialName("user_apps")
    val userApps: List<AppDesc>,
) {
    companion object {
        @JvmField
        val EMPTY = Apps(
            systemApps = emptyList(),
            userApps = emptyList(),
        )
    }
}