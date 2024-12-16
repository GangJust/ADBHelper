package adb.entity

import kotlinx.serialization.Serializable

@Serializable
data class Screenshot(
    // 媒体类型
    val mimetype: String,
    // 宽度
    val width: Long,
    // 高度
    val height: Long,
    // 截图数据
    val data: ByteArray,
) {

    // Gson 需要保留一个空参构造方法
    // Gson Need to retain an empty parameter construction method
    constructor() : this(
        mimetype = "",
        width = 0,
        height = 0,
        data = ByteArray(0),
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Screenshot

        if (mimetype != other.mimetype) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mimetype.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

    companion object {
        @JvmField
        val EMPTY = Screenshot(
            mimetype = "",
            width = 0,
            height = 0,
            data = ByteArray(0),
        )
    }
}