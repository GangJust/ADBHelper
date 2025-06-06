package io.github.adbhelper.app.viewmodel

import io.github.adbhelper.adb.AdbServer
import io.github.adbhelper.adb.entity.Device
import io.github.adbhelper.adb.entity.FileDesc
import io.github.adbhelper.entity.Bookmark
import io.github.adbhelper.i18n.StringRes
import io.github.adbhelper.mvi.BaseAction
import io.github.adbhelper.mvi.BaseMVI
import io.github.adbhelper.mvi.MsgCallback
import io.github.adbhelper.utils.CacheUtils
import io.github.adbhelper.utils.PathUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import java.io.File

sealed class FileManagerAction : BaseAction() {

    data class BookmarkDialog(
        val isShowing: Boolean,
    ) : FileManagerAction()

    data class BookmarkEditDialog(
        val bookmark: Bookmark?,
    ) : FileManagerAction()

    data class GetBookmarks(
        val device: Device,
    ) : FileManagerAction()

    data class AddBookmark(
        val device: Device,
        val bookmark: Bookmark,
        val callback: MsgCallback,
    ) : FileManagerAction()

    data class SaveBookmark(
        val device: Device,
        val bookmark: Bookmark,
        val callback: MsgCallback,
    ) : FileManagerAction()

    data class DeleteBookmark(
        val device: Device,
        val bookmark: Bookmark,
        val callback: MsgCallback,
    ) : FileManagerAction()

    data class GoBookmark(
        val device: Device,
        val bookmark: Bookmark,
    ) : FileManagerAction()

    data class GetFileList(
        val device: Device,
        val path: String = "/",
    ) : FileManagerAction()

    data class CurrPath(
        val path: String,
    ) : FileManagerAction()

    data class DetailDialog(
        val desc: FileDesc?,
    ) : FileManagerAction()

    data class DeleteDialog(
        val desc: FileDesc?,
    ) : FileManagerAction()

    data class GoDirectory(
        val device: Device,
        val desc: FileDesc,
    ) : FileManagerAction()

    data class Pull(
        val device: Device,
        val desc: FileDesc,
        val callback: MsgCallback,
    ) : FileManagerAction()

    data class Push(
        val device: Device,
        val localPath: String,
        val callback: MsgCallback,
    ) : FileManagerAction()

    data class Delete(
        val device: Device,
        val desc: FileDesc,
        val callback: MsgCallback,
    ) : FileManagerAction()

    data class Permissions(
        val device: Device,
        val desc: FileDesc,
    ) : FileManagerAction()
}

class FileManagerViewModel() : BaseMVI<FileManagerAction>() {
    private val _isWaiting = MutableStateFlow(false)
    private val _showBookmark = MutableStateFlow(false)
    private val _showBookmarkEdit = MutableStateFlow<Bookmark?>(null)
    private val _showDetail = MutableStateFlow<FileDesc?>(null)
    private val _showDelete = MutableStateFlow<FileDesc?>(null)

    private val _bookmarks = MutableStateFlow(listOf<Bookmark>())
    private val _currPath = MutableStateFlow("/")
    private val _fileList = MutableStateFlow(listOf<FileDesc>())

    val isWaiting: StateFlow<Boolean> = _isWaiting
    val showBookmark: StateFlow<Boolean> = _showBookmark
    val showBookmarkEdit: StateFlow<Bookmark?> = _showBookmarkEdit
    val showDetail: StateFlow<FileDesc?> = _showDetail
    val showDelete: StateFlow<FileDesc?> = _showDelete

    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks
    val currPath: StateFlow<String> = _currPath
    val fileList: StateFlow<List<FileDesc>> = _fileList

    private val bookmarksCache = "bookmarks.json"

    private fun _readBookmarksCache(device: Device) {
        val bookmarks = CacheUtils.readString("${device.displaySerialNo}/$bookmarksCache")
        _bookmarks.value = runCatching {
            Json.decodeFromString<List<Bookmark>>(bookmarks)
        }.getOrDefault(emptyList())
    }

    private fun _saveBookmarksCache(device: Device) {
        val bookmarks = Json.encodeToString(_bookmarks.value)
        CacheUtils.writeString("${device.displaySerialNo}/$bookmarksCache", bookmarks)
    }

    // 书签对话框
    // Bookmark dialog
    private fun handleBookmarkDialog(isShowing: Boolean) {
        _showBookmark.value = isShowing
    }

    // 书签编辑对话框
    // Bookmark edit dialog
    private fun handleBookmarkEditDialog(bookmark: Bookmark?) {
        _showBookmarkEdit.value = bookmark
    }

    // 获取书签列表
    // Get bookmark list
    private fun handleBookmarks(device: Device) {
        singleLaunchIO("getBookmarks") {
            _readBookmarksCache(device)
        }
    }

    // 添加书签
    // Add bookmark
    private fun handleAddBookmark(
        device: Device,
        bookmark: Bookmark,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("onAddBookmark") {
            runCatching {
                _bookmarks.value += bookmark
                _saveBookmarksCache(device)
                msgCallback.onMsg(StringRes.locale.bookmarkAddedSuccess)
            }.onFailure {
                msgCallback.onMsg(String.format(StringRes.locale.bookmarkAddedFailure, it.message))
            }
        }
    }

    // 保存书签
    // Save bookmark
    private fun handleSaveBookmark(
        device: Device,
        bookmark: Bookmark,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("onSaveBookmark") {
            runCatching {
                val optBookmarks = _bookmarks.value.toMutableList()
                val firstIndex = optBookmarks.indexOfFirst {
                    bookmark.created == it.created
                }
                if (firstIndex == -1) {
                    msgCallback.onMsg(StringRes.locale.bookmarkNotFound)
                    return@singleLaunchIO
                }

                optBookmarks[firstIndex] = bookmark
                _bookmarks.value = optBookmarks
                _saveBookmarksCache(device)

                msgCallback.onMsg(StringRes.locale.bookmarkSavedSuccess)
            }.onFailure {
                msgCallback.onMsg(String.format(StringRes.locale.bookmarkSavedFailure, it.message))
            }
        }
    }

    // 删除书签
    // Delete bookmark
    private fun handleDeleteBookmark(
        device: Device,
        bookmark: Bookmark,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("onDeleteBookmark") {
            runCatching {
                val optBookmarks = _bookmarks.value.filter { it.created != bookmark.created }
                if (optBookmarks.size == _bookmarks.value.size) {
                    msgCallback.onMsg(StringRes.locale.bookmarkNotFound)
                    return@singleLaunchIO
                }

                _bookmarks.value = optBookmarks
                _saveBookmarksCache(device)
                msgCallback.onMsg(StringRes.locale.bookmarkDeletedSuccess)
            }.onFailure {
                msgCallback.onMsg(
                    String.format(
                        StringRes.locale.bookmarkDeletedFailure,
                        it.message
                    )
                )
            }
        }
    }

    // 跳转书签
    // Go bookmark
    private fun handleGoBookmark(
        device: Device,
        bookmark: Bookmark,
    ) {
        if (bookmark.isFile) {
            val path = bookmark.path.substringBeforeLast("/")
            handleGetFileList(device, path)
        } else {
            handleGetFileList(device, bookmark.path)
        }
    }

    // 获取文件列表
    // Get file list
    private fun handleGetFileList(
        device: Device,
        path: String,
    ) {
        val newPath = path.removeSuffix("/") + "/"
        _currPath.value = newPath

        singleLaunchIO("getFileList") {
            _fileList.value = listOf() // 清空重新加载| Clear and reload

            var files = AdbServer.instance.getFiles(device, newPath)

            // 过滤以下情况: 1、目录不存在, 2、无访问权限，3、非目录
            // Filter the following cases: 1. The directory does not exist, 2. No access permission, 3. Not a directory
            if (files.isNotEmpty()) {
                files = files.filter { !it.isNoSuch && !it.isNoPermission && !it.isNotDirectory }
            }

            // 部分手机前两个并不常是 . 和 .. 如: oppo的某机型, 避免混淆, 先手动移除掉
            // For some phones, the first two are not often . and .., such as: a certain model of oppo, to avoid confusion, remove them manually first
            if (files.size >= 2) {
                if (files[0].name == "." && files[1].name == "..") {
                    files = files.toMutableList().also {
                        it.removeAt(1)
                        it.removeAt(0)
                    }
                }
            }

            // 排序| Sort
            files = files.sortedBy { it }

            // 手动添加返回上级标识
            // Manually add the return to the parent directory identifier
            _fileList.value += FileDesc.byPath(newPath)
                .copy(name = "..", kind = FileDesc.Kind.Superior.value)

            // 最终列表| Final list
            for (file in files) {
                if (!isActive) { // 非活跃状态, 清空并结束
                    _fileList.value = listOf()
                    return@singleLaunchIO
                }

                if (file.isLink) { // 对链接获取指向类型，重新获取| For links, get the type of the target and get it again
                    val kind = AdbServer.instance.getFileKind(device, file)
                    if (kind == "directory") {
                        _fileList.value += file.copy(kind = "link-directory")
                    } else {
                        _fileList.value += file.copy(kind = "link-file")
                    }
                } else {
                    _fileList.value += file
                }
            }
        }
    }

    // 详情对话框
    // Detail dialog
    private fun handleDetailDialog(desc: FileDesc?) {
        _showDetail.value = desc
    }

    // 删除对话框
    // Delete dialog
    private fun handleDeleteDialog(desc: FileDesc?) {
        _showDelete.value = desc
    }

    // 更新当前路径
    // Update current path
    private fun handleCurrPath(path: String) {
        _currPath.value = path
    }

    // 跳转文件夹
    // Go directory
    private fun handleGoDirectory(
        device: Device,
        desc: FileDesc,
    ) {
        if (desc.kind == "superior") { // 返回上一页| Return to the previous page
            handleGetFileList(device, desc.path.removeSuffix("/").substringBeforeLast("/") + "/")
        } else if (desc.isDirectory || desc.isLinkDirectory) { // 文件夹| Directory
            handleGetFileList(device, desc.absolutePath + "/")
        }
    }

    // 拉取文件
    // Pull file
    private fun handlePull(
        device: Device,
        desc: FileDesc,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("onPull") {
            _isWaiting.value = true
            val download = PathUtils.getDownloadPath()
            val localPath = File(download, desc.safeName).absolutePath
            val result = AdbServer.instance.pullFile(device, desc.absolutePath, localPath).trim()

            if (result.contains("pulled")) {
                PathUtils.openDir(download)
            }

            if (result.isEmpty()) {
                msgCallback.onMsg(StringRes.locale.operSuccess)
            } else {
                msgCallback.onMsg(result)
            }
            _isWaiting.value = false
        }
    }

    // 推送文件
    // Push file
    private fun handlePush(
        device: Device,
        path: String,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("onPush") {
            _isWaiting.value = true
            val result = AdbServer.instance.pushFile(device, path, currPath.value).trim()

            if (result.contains("pushed")) {
                handleGetFileList(device, currPath.value)
            }

            if (result.isEmpty()) {
                msgCallback.onMsg(StringRes.locale.operSuccess)
            } else {
                msgCallback.onMsg(result)
            }
            _isWaiting.value = false
        }
    }

    // 删除文件|文件夹
    // Delete file|folder
    private fun handleDelete(
        device: Device,
        desc: FileDesc,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("onDelete") {
            _isWaiting.value = true
            val result = AdbServer.instance.shellSyn(device, "rm -rf '${desc.absolutePath}'").trim()
            if (result.isEmpty()) {
                handleGetFileList(device, desc.path)
                msgCallback.onMsg(StringRes.locale.operSuccess)
            } else {
                msgCallback.onMsg(result)
            }
            _isWaiting.value = false
        }
    }

    // 权限
    // Permissions
    private fun handlePermissions(
        device: Device,
        desc: FileDesc,
    ) {
        // todo future
    }

    override fun dispatch(action: FileManagerAction) {
        when (action) {
            is FileManagerAction.BookmarkDialog -> handleBookmarkDialog(action.isShowing)
            is FileManagerAction.BookmarkEditDialog -> handleBookmarkEditDialog(action.bookmark)
            is FileManagerAction.GetBookmarks -> handleBookmarks(action.device)
            is FileManagerAction.AddBookmark -> handleAddBookmark(
                action.device,
                action.bookmark,
                action.callback
            )

            is FileManagerAction.SaveBookmark -> handleSaveBookmark(
                action.device,
                action.bookmark,
                action.callback
            )

            is FileManagerAction.DeleteBookmark -> handleDeleteBookmark(
                action.device,
                action.bookmark,
                action.callback
            )

            is FileManagerAction.GoBookmark -> handleGoBookmark(action.device, action.bookmark)
            is FileManagerAction.GetFileList -> handleGetFileList(action.device, action.path)
            is FileManagerAction.DetailDialog -> handleDetailDialog(action.desc)
            is FileManagerAction.DeleteDialog -> handleDeleteDialog(action.desc)
            is FileManagerAction.CurrPath -> handleCurrPath(action.path)
            is FileManagerAction.GoDirectory -> handleGoDirectory(action.device, action.desc)
            is FileManagerAction.Pull -> handlePull(action.device, action.desc, action.callback)
            is FileManagerAction.Push -> handlePush(action.device, action.localPath, action.callback)
            is FileManagerAction.Delete -> handleDelete(action.device, action.desc, action.callback)
            is FileManagerAction.Permissions -> handlePermissions(action.device, action.desc)
        }
    }
}