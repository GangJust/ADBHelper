package adb.entity

data class FileDesc(
    // 索引号
    val inode: String,
    // 权限字符串
    val permissionStr: String,
    // 链接数
    val linkCount: String,
    // 所有者
    val owner: String,
    // 所属组
    val group: String,
    // 文件大小
    val size: String,
    // 修改时间
    val datetime: String,
    // 文件名
    val name: String,
    // 文件类型
    val kind: String,
    // 文件路径
    val path: String,
) : Comparable<FileDesc> {

    // Gson 需要保留一个空参构造方法
    // Gson Need to retain an empty parameter construction method
    constructor() : this(
        inode = "",
        permissionStr = "",
        linkCount = "",
        owner = "",
        group = "",
        size = "",
        datetime = "",
        name = "",
        kind = "",
        path = ""
    )

    enum class Kind(val value: String) {
        // 上级目录
        Superior("superior"),

        // 不存在
        NoSuch("no-such"),

        // 无权限
        NoPermission("no-permission"),

        // 非目录
        NotDirectory("not-directory"),

        // 链接目录
        LinkDirectory("link-directory"),

        // 链接文件
        LinkFile("link-file"),

        // 目录
        Directory("directory"),

        // 文件
        File("file"),
    }

    override fun compareTo(other: FileDesc): Int {
        return kind.compareTo(other.kind)
    }

    val isSuperior
        get() = kind == Kind.Superior.value

    val isNoSuch
        get() = kind == Kind.NoSuch.value

    val isNoPermission
        get() = kind == Kind.NoPermission.value

    val isNotDirectory
        get() = kind == Kind.NotDirectory.value

    val isLink: Boolean
        get() = kind.startsWith("link")

    val isLinkDirectory: Boolean
        get() = kind == Kind.LinkDirectory.value

    val isLinkFile: Boolean
        get() = kind == Kind.LinkFile.value

    val isDirectory: Boolean
        get() = kind == Kind.Directory.value

    val isFile: Boolean
        get() = kind == Kind.File.value

    val isDocument: Boolean
        get() = isFile && (name.endsWith(".csv", true) ||
                name.endsWith(".html", true) ||
                name.endsWith(".xml", true) ||
                name.endsWith(".json", true) ||
                name.endsWith(".prop", true) ||
                name.endsWith(".rtf", true) ||
                name.endsWith(".odt", true) ||
                name.endsWith(".pdf", true) ||
                name.endsWith(".doc", true) ||
                name.endsWith(".docx", true) ||
                name.endsWith(".xls", true) ||
                name.endsWith(".xlsx", true) ||
                name.endsWith(".ppt", true) ||
                name.endsWith(".pptx", true) ||
                name.endsWith(".txt", true))

    val isZip: Boolean
        get() = isFile && (name.endsWith(".zip", true) ||
                name.endsWith(".rar", true) ||
                name.endsWith(".jar", true))

    val isApk: Boolean
        get() = isFile && (name.endsWith(".apk", true) ||
                name.endsWith(".apk.1", true))

    val isImage: Boolean
        get() = isFile && (name.endsWith(".jpg", true) ||
                name.endsWith(".jpeg", true) ||
                name.endsWith(".png", true) ||
                name.endsWith(".bmp", true) ||
                name.endsWith(".svg", true) ||
                name.endsWith(".webp", true) ||
                name.endsWith(".gif", true))

    val isVideo: Boolean
        get() = isFile && (name.endsWith(".mp4", true) ||
                name.endsWith(".rmvb", true) ||
                name.endsWith(".mov", true) ||
                name.endsWith(".wmv", true) ||
                name.endsWith(".mkv", true) ||
                name.endsWith(".avi", true))

    val isMusic: Boolean
        get() = isFile && (name.endsWith(".mp3", true) ||
                name.endsWith(".aac", true) ||
                name.endsWith(".wma", true) ||
                name.endsWith(".wav", true) ||
                name.endsWith(".m4a", true) ||
                name.endsWith(".flac", true) ||
                name.endsWith(".alac", true) ||
                name.endsWith(".ape", true) ||
                name.endsWith(".aiff", true) ||
                name.endsWith(".midi", true) ||
                name.endsWith(".ac3", true) ||
                name.endsWith(".dts", true) ||
                name.endsWith(".mar", true) ||
                name.endsWith(".ogg", true))

    val safeName: String
        get() = if (isLink) {
            // 链接取箭头指向前的部分(link -> target)
            // The link takes the part of the arrow pointing forward (link -> target)
            name.split(" ").first()
        } else {
            name
        }

    val absolutePath: String
        get() = path + safeName

    companion object {
        @JvmField
        val EMPTY = FileDesc(
            inode = "",
            permissionStr = "",
            linkCount = "",
            owner = "",
            group = "",
            size = "",
            datetime = "",
            name = "",
            kind = "",
            path = ""
        )

        /**
         * 通过路径创建文件描述，只包含路径信息
         * Create file descriptions by path, containing only path information
         *
         * @param path 文件路径|file path
         */
        fun byPath(path: String): FileDesc {
            return FileDesc(
                inode = "",
                permissionStr = "",
                linkCount = "",
                owner = "",
                group = "",
                size = "",
                datetime = "",
                name = "",
                kind = "",
                path = path
            )
        }
    }
}
