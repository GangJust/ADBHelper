package app.viewmodel

import adb.AdbServer
import adb.entity.AppDesc
import adb.entity.Device
import androidx.compose.foundation.lazy.LazyListState
import entity.Apps
import i18n.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mvi.BaseAction
import mvi.BaseViewModel
import mvi.MsgCallback
import mvi.MsgResult
import utils.CacheUtils
import utils.PathUtils

sealed class AppListAction : BaseAction() {
    data class SelectedTab(val index: Int) : AppListAction()
    data class UpdateSearchText(val text: String) : AppListAction()
    data class GetAppList(val device: Device) : AppListAction()

    data class RefreshAppList(
        val device: Device,
        val reload: Boolean = false,
    ) : AppListAction()

    data class RefreshAppItem(
        val device: Device,
        val desc: AppDesc,
    ) : AppListAction()

    data class InstallApk(
        val device: Device,
        val apkPath: String,
        val msgCallback: MsgCallback,
    ) : AppListAction()

    data class UninstallApp(
        val device: Device,
        val desc: AppDesc,
        val msgCallback: MsgCallback,
    ) : AppListAction()

    data class ClearDataApp(
        val device: Device,
        val desc: AppDesc,
        val msgCallback: MsgCallback,
    ) : AppListAction()

    data class KillProcessApp(
        val device: Device,
        val desc: AppDesc,
        val msgCallback: MsgCallback,
    ) : AppListAction()

    data class LaunchActivity(
        val device: Device,
        val desc: AppDesc,
        val msgCallback: MsgCallback,
    ) : AppListAction()

    data class ExportApp(
        val device: Device,
        val desc: AppDesc,
        val msgResult: MsgResult<String?>,
    ) : AppListAction()
}

class AppListViewModel : BaseViewModel<AppListAction>() {
    private val _isWaiting = MutableStateFlow(false)
    private val _currentTabIndex = MutableStateFlow(0)
    private val _allAppList = MutableStateFlow(listOf<AppDesc>())
    private val _allAppsListState: LazyListState = LazyListState()
    private val _systemAppList = MutableStateFlow(listOf<AppDesc>())
    private val _systemAppListState: LazyListState = LazyListState()
    private val _userAppList = MutableStateFlow(listOf<AppDesc>())
    private val _userAppListState: LazyListState = LazyListState()
    private val _searchText = MutableStateFlow("")
    private val _searchAppList = MutableStateFlow(listOf<AppDesc>())
    private val _searchAppState: LazyListState = LazyListState()

    val isWaiting: StateFlow<Boolean> = _isWaiting
    val currentTabIndex: StateFlow<Int> = _currentTabIndex
    val allAppList: StateFlow<List<AppDesc>> = _allAppList
    val systemAppList: StateFlow<List<AppDesc>> = _systemAppList
    val userAppList: StateFlow<List<AppDesc>> = _userAppList
    val searchText: StateFlow<String> = _searchText
    val searchAppList: StateFlow<List<AppDesc>> = _searchAppList

    private val appCache = "apps.json"

    // private val mutex = Mutex()

    // 读取缓存的应用列表
    // Read cached application list
    private fun _readAppsCache(device: Device) {
        val content = CacheUtils.readString("${device.displaySerialNo}/$appCache")
        val apps = runCatching { Json.decodeFromString<Apps>(content) }.getOrDefault(Apps.EMPTY)
        _systemAppList.value = apps.systemApps
        _userAppList.value = apps.userApps
        _allAppList.value = _systemAppList.value + _userAppList.value
    }

    // 保存应用列表至缓存
    // Save the application list to cache
    private fun _saveAppsCache(device: Device) {
        val apps = Apps(_systemAppList.value, _userAppList.value)
        val content = runCatching { Json.encodeToString(apps) }.getOrDefault("{}")
        CacheUtils.writeString("${device.displaySerialNo}/$appCache", content)
    }

    // 当前列表滚动状态
    // Current list scrolling status
    fun currentListState(index: Int): LazyListState {
        return when (index) {
            0 -> _allAppsListState
            1 -> _systemAppListState
            2 -> _userAppListState
            else -> _searchAppState
        }
    }

    // 当前列表的数据
    // Data for the current list
    fun currentAppList(index: Int): StateFlow<List<AppDesc>> {
        return when (index) {
            0 -> allAppList
            1 -> systemAppList
            2 -> userAppList
            else -> searchAppList
        }
    }

    // 选中指定的 Tab
    // Select the specified tab
    private fun selectedTab(index: Int) {
        _currentTabIndex.value = index
    }

    // 更新搜索框的内容
    // Update the content of the search box
    private fun updateSearchText(text: String) {
        _searchText.value = text

        // 按关键字过滤符合条件的应用
        // Filter eligible apps by keyword
        _searchAppList.value = allAppList.value.filter {
            it.packageName.contains(text)
        }

        // 当搜索框为空时，切换到全部列表
        // When the search box is empty, switch to the full list
        if (text.isEmpty() && currentTabIndex.value == 3) {
            selectedTab(0)
        }
    }

    // 清除应用列表缓存
    // Clear the app list cache
    private fun clearAppsCache() {
        _systemAppList.value = emptyList()
        _userAppList.value = emptyList()
        _allAppList.value = emptyList()
    }

    // 获取应用列表
    // Get app list
    private fun getAppList(device: Device) {
        singleLaunchIO("getAppList") {
            _readAppsCache(device)
            refreshAppList(device, false)
        }
    }

    // 刷新应用列表
    // Refresh app list
    private fun refreshAppList(
        device: Device,
        reload: Boolean,
    ) {
        singleLaunchIO("refreshAppList") {
            // clear cache
            if (reload) {
                clearAppsCache()
            }

            val systemAsync = async(Dispatchers.IO) {
                refreshAppListHelper(device, true, _systemAppList)
            }
            val userAsync = async(Dispatchers.IO) {
                refreshAppListHelper(device, false, _userAppList)
            }

            withContext(Dispatchers.Main) { _isWaiting.value = true }
            val systemResult = systemAsync.await()
            val userResult = userAsync.await()
            withContext(Dispatchers.Main) { _isWaiting.value = false }

            _saveAppsCache(device)
        }
    }

    private suspend fun refreshAppListHelper(
        device: Device,
        isSystem: Boolean,
        appList: MutableStateFlow<List<AppDesc>>,
    ): Pair<Set<String>, Set<String>> {
        val currPkg = appList.value.map { it.packageName }.toSet()
        val remotePkg = AdbServer.instance.getPackages(device, isSystem).toSet()

        // reduce
        val reducePackages = currPkg - remotePkg
        withContext(Dispatchers.Main) {
            reducePackages.forEach { pkg ->
                appList.value = appList.value.filter { desc -> desc.packageName != pkg }
                _allAppList.value = _allAppList.value.filter { desc -> desc.packageName != pkg }
                updateSearchText(_searchText.value)
            }
        }

        // increase
        val increasePackages = remotePkg - currPkg
        withContext(Dispatchers.IO) {
            increasePackages.forEach { pkg ->
                if (!isActive)
                    return@forEach

                // mutex.lock()
                val desc = AdbServer.instance.getAppDesc(device, pkg)
                // mutex.unlock()

                withContext(Dispatchers.Main) {
                    appList.value += desc
                    _allAppList.value += desc
                    updateSearchText(_searchText.value)
                }
            }
        }

        return Pair(reducePackages, increasePackages) // reduce and increase
    }

    // 刷新单个应用
    // Refresh a single app
    private fun refreshAppItem(
        device: Device,
        desc: AppDesc,
    ) {
        singleLaunchIO("refreshAppItem") {
            val remotePkg = AdbServer.instance.getPackages(device, desc.isSystem)
            val optionList = if (desc.isSystem) _systemAppList else _userAppList
            if (remotePkg.contains(desc.packageName)) { // is replaced
                val newDesc = AdbServer.instance.getAppDesc(device, desc.packageName)

                //
                val optList = optionList.value.toMutableList()
                val optIndexOf = optList.indexOfFirst { it.packageName == desc.packageName }
                optList.removeAt(optIndexOf)
                optList.add(optIndexOf, newDesc)
                optionList.value = optList

                //
                val allList = _allAppList.value.toMutableList()
                val allIndexOf = allList.indexOf(desc)
                allList.removeAt(allIndexOf)
                allList.add(allIndexOf, newDesc)
                _allAppList.value = allList

                //
                updateSearchText(_searchText.value)
            } else { // is uninstalled
                optionList.value = optionList.value.filter { it.packageName != desc.packageName }
                _allAppList.value = _allAppList.value.filter { it.packageName != desc.packageName }
                updateSearchText(_searchText.value)
            }
        }
    }

    // 安装 APK
    // install apk
    private fun installApk(
        device: Device,
        apkPath: String,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("installApk") {
            val result = AdbServer.instance.installApk(device, apkPath)
            val trim = result.trim()
            if (trim.isEmpty()) {
                msgCallback.onMsg(StringRes.locale.operSuccess)
            } else {
                msgCallback.onMsg(trim)
            }
        }
    }

    // 卸载应用
    // uninstall app
    private fun uninstallApp(
        device: Device,
        desc: AppDesc,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("uninstallApp") {
            val result = AdbServer.instance.uninstallApk(device, desc.packageName)
            val trim = result.trim()
            if (trim.isEmpty()) {
                msgCallback.onMsg(StringRes.locale.operSuccess)
            } else {
                msgCallback.onMsg(trim)
            }
        }
    }

    // 清除应用数据
    // clear app data
    private fun clearDataApp(
        device: Device,
        desc: AppDesc,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("clearDataApp") {
            val result = AdbServer.instance.shellSyn(device, "pm clear ${desc.packageName}")
            val trim = result.trim()
            if (trim.isEmpty()) {
                msgCallback.onMsg(StringRes.locale.operSuccess)
            } else {
                msgCallback.onMsg(trim)
            }
        }
    }

    // 杀死应用进程
    // kill app all process
    private fun killProcessApp(
        device: Device,
        desc: AppDesc,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("killProcessApp") {
            val result = AdbServer.instance.shellSyn(device, "am force-stop ${desc.packageName}")
            val trim = result.trim()
            if (trim.isEmpty()) {
                msgCallback.onMsg(StringRes.locale.operSuccess)
            } else {
                msgCallback.onMsg(trim)
            }
        }
    }

    // 启动应用 Activity
    // launch activity
    private fun launchActivity(
        device: Device,
        desc: AppDesc,
        msgCallback: MsgCallback,
    ) {
        singleLaunchIO("launchActivity") {
            val result = AdbServer.instance.shellSyn(device, "am start -n ${desc.launcherActivity}")
            val trim = result.trim()
            if (trim.isEmpty()) {
                msgCallback.onMsg(StringRes.locale.operSuccess)
            } else {
                msgCallback.onMsg(trim)
            }
        }
    }

    // 导出应用
    // export apk
    private fun exportApp(
        device: Device,
        desc: AppDesc,
        msgResult: MsgResult<String?>,
    ) {
        singleLaunchIO("exportApp") {
            val downloadPath = PathUtils.getDownloadPath()
            val result = AdbServer.instance.pullFile(
                device = device,
                remotePath = desc.installPath,
                localPath = "${downloadPath}/${desc.packageName}_${desc.versionName}_${desc.versionCode}.apk",
            )
            val trim = result.trim()
            if (trim.isEmpty()) {
                msgResult.onResult(StringRes.locale.operSuccess, downloadPath)
            } else {
                msgResult.onResult(trim, downloadPath)
            }
        }
    }

    override fun dispatch(action: AppListAction) {
        when (action) {
            is AppListAction.SelectedTab -> selectedTab(action.index)
            is AppListAction.UpdateSearchText -> updateSearchText(action.text)
            is AppListAction.GetAppList -> getAppList(action.device)
            is AppListAction.RefreshAppList -> refreshAppList(action.device, action.reload)
            is AppListAction.RefreshAppItem -> refreshAppItem(action.device, action.desc)
            is AppListAction.InstallApk -> installApk(
                action.device,
                action.apkPath,
                action.msgCallback
            )

            is AppListAction.UninstallApp -> uninstallApp(
                action.device,
                action.desc,
                action.msgCallback
            )

            is AppListAction.ClearDataApp -> clearDataApp(
                action.device,
                action.desc,
                action.msgCallback
            )

            is AppListAction.KillProcessApp -> killProcessApp(
                action.device,
                action.desc,
                action.msgCallback
            )

            is AppListAction.LaunchActivity -> launchActivity(
                action.device,
                action.desc,
                action.msgCallback
            )

            is AppListAction.ExportApp -> exportApp(action.device, action.desc, action.msgResult)
        }
    }
}