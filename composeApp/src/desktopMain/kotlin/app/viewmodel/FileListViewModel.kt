package app.viewmodel

import adb.AdbServer
import adb.entity.AppDesc
import adb.entity.Device
import adb.entity.FileDesc
import entity.Bookmark
import i18n.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import mvi.BaseAction
import mvi.BaseViewModel
import mvi.MsgCallback
import utils.CacheUtils
import utils.PathUtils
import java.io.File

sealed class FileListAction : BaseAction() {

    data class BookmarkDialog(
        val isShowing: Boolean,
    ) : FileListAction()

    data class BookmarkEditDialog(
        val bookmark: Bookmark?,
    ) : FileListAction()

    data class GetBookmarks(
        val device: Device,
    ) : FileListAction()

    data class OnAddBookmark(
        val device: Device,
        val bookmark: Bookmark,
        val callback: MsgCallback,
    ) : FileListAction()

    data class OnSaveBookmark(
        val device: Device,
        val bookmark: Bookmark,
        val callback: MsgCallback,
    ) : FileListAction()

    data class OnDeleteBookmark(
        val device: Device,
        val bookmark: Bookmark,
        val callback: MsgCallback,
    ) : FileListAction()

    data class GoBookmark(
        val device: Device,
        val bookmark: Bookmark,
    ) : FileListAction()

    data class GetFileList(
        val device: Device,
        val path: String = "/",
    ) : FileListAction()

    data class OnCurrPath(
        val path: String,
    ) : FileListAction()

    data class DetailDialog(
        val desc: FileDesc?,
    ) : FileListAction()

    data class DeleteDialog(
        val desc: FileDesc?,
    ) : FileListAction()

    data class GoDirectory(
        val device: Device,
        val desc: FileDesc,
    ) : FileListAction()

    data class OnPull(
        val device: Device,
        val desc: FileDesc,
        val callback: MsgCallback,
    ) : FileListAction()

    data class OnPush(
        val device: Device,
        val localPath: String,
        val callback: MsgCallback,
    ) : FileListAction()

    data class OnDelete(
        val device: Device,
        val desc: FileDesc,
        val callback: MsgCallback,
    ) : FileListAction()

    data class OnPermissions(
        val device: Device,
        val desc: FileDesc,
    ) : FileListAction()
}

class FileListViewModel : BaseViewModel<FileListAction>() {
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
        val arr = CacheUtils.readJsonArr("${device.displaySerialNo}/$bookmarksCache")
        _bookmarks.value = arr.map { CacheUtils.gson.fromJson(it, Bookmark::class.java) }
    }

    private fun _saveBookmarksCache(device: Device) {
        val bookmarks = CacheUtils.gson.toJsonTree(_bookmarks.value)
        CacheUtils.writeJson("${device.displaySerialNo}/$bookmarksCache", bookmarks)
    }

    // 书签对话框
    // Bookmark dialog
    private fun onBookmarkDialog(isShowing: Boolean) {
        _showBookmark.value = isShowing
    }

    // 书签编辑对话框
    // Bookmark edit dialog
    private fun onBookmarkEditDialog(bookmark: Bookmark?) {
        _showBookmarkEdit.value = bookmark
    }

    // 获取书签列表
    // Get bookmark list
    private fun getBookmarks(device: Device) {
        singleLaunchIO("getBookmarks") {
            _readBookmarksCache(device)
        }
    }

    // 添加书签
    // Add bookmark
    private fun onAddBookmark(
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
    private fun onSaveBookmark(
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
    private fun onDeleteBookmark(
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
                msgCallback.onMsg(String.format(StringRes.locale.bookmarkDeletedFailure, it.message))
            }
        }
    }

    // 跳转书签
    // Go bookmark
    private fun goBookmark(
        device: Device,
        bookmark: Bookmark,
    ) {
        if (bookmark.isFile) {
            val path = bookmark.path.substringBeforeLast("/")
            getFileList(device, path)
        } else {
            getFileList(device, bookmark.path)
        }
    }

    // 获取文件列表
    // Get file list
    private fun getFileList(
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
            _fileList.value += FileDesc.byPath(newPath).copy(name = "..", kind = FileDesc.Kind.Superior.value)

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
    private fun onDetailDialog(desc: FileDesc?) {
        _showDetail.value = desc
    }

    // 删除对话框
    // Delete dialog
    private fun onDeleteDialog(desc: FileDesc?) {
        _showDelete.value = desc
    }

    // 更新当前路径
    // Update current path
    private fun onCurrPath(path: String) {
        _currPath.value = path
    }

    // 跳转文件夹
    // Go directory
    private fun goDirectory(
        device: Device,
        desc: FileDesc,
    ) {
        if (desc.kind == "superior") { // 返回上一页| Return to the previous page
            getFileList(device, desc.path.removeSuffix("/").substringBeforeLast("/") + "/")
        } else if (desc.isDirectory || desc.isLinkDirectory) { // 文件夹| Directory
            getFileList(device, desc.absolutePath + "/")
        }
    }

    // 拉取文件
    // Pull file
    private fun onPull(
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
    private fun onPush(
        device: Device,
        path: String,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("onPush") {
            _isWaiting.value = true
            val result = AdbServer.instance.pushFile(device, path, currPath.value).trim()

            if (result.contains("pushed")) {
                getFileList(device, currPath.value)
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
    private fun onDelete(
        device: Device,
        desc: FileDesc,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("onDelete") {
            _isWaiting.value = true
            val result = AdbServer.instance.shellSyn(device, "rm -rf '${desc.absolutePath}'").trim()
            if (result.isEmpty()) {
                getFileList(device, desc.path)
                msgCallback.onMsg(StringRes.locale.operSuccess)
            } else {
                msgCallback.onMsg(result)
            }
            _isWaiting.value = false
        }
    }

    // 权限
    // Permissions
    private fun onPermissions(
        device: Device,
        desc: FileDesc,
    ) {
        // todo future
    }

    override fun dispatch(action: FileListAction) {
        when (action) {
            is FileListAction.BookmarkDialog -> onBookmarkDialog(action.isShowing)
            is FileListAction.BookmarkEditDialog -> onBookmarkEditDialog(action.bookmark)
            is FileListAction.GetBookmarks -> getBookmarks(action.device)
            is FileListAction.OnAddBookmark -> onAddBookmark(action.device, action.bookmark, action.callback)
            is FileListAction.OnSaveBookmark -> onSaveBookmark(action.device, action.bookmark, action.callback)
            is FileListAction.OnDeleteBookmark -> onDeleteBookmark(action.device, action.bookmark, action.callback)
            is FileListAction.GoBookmark -> goBookmark(action.device, action.bookmark)
            is FileListAction.GetFileList -> getFileList(action.device, action.path)
            is FileListAction.DetailDialog -> onDetailDialog(action.desc)
            is FileListAction.DeleteDialog -> onDeleteDialog(action.desc)
            is FileListAction.OnCurrPath -> onCurrPath(action.path)
            is FileListAction.GoDirectory -> goDirectory(action.device, action.desc)
            is FileListAction.OnPull -> onPull(action.device, action.desc, action.callback)
            is FileListAction.OnPush -> onPush(action.device, action.localPath, action.callback)
            is FileListAction.OnDelete -> onDelete(action.device, action.desc, action.callback)
            is FileListAction.OnPermissions -> onPermissions(action.device, action.desc)
        }
    }
}