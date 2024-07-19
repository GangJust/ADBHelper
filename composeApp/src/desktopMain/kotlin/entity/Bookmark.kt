package entity

import adb.entity.FileDesc
import java.io.Serializable

data class Bookmark(
    val name: String,
    val path: String,
    val kind: String,
    val created: Long = System.currentTimeMillis(),
) {
    // Gson 需要保留一个空参构造方法
    // Gson Need to retain an empty parameter construction method
    constructor() : this(
        name = "",
        path = "",
        kind = "",
        created = 0L,
    )

    val isFile: Boolean
        get() = kind == FileDesc.Kind.File.value

    val isDirectory: Boolean
        get() = kind == FileDesc.Kind.Directory.value
}
