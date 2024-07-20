package app.viewmodel

import adb.AdbServer
import adb.entity.Activity
import adb.entity.Device
import adb.entity.Screenshot
import i18n.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mvi.BaseAction
import mvi.BaseViewModel
import mvi.MsgCallback
import mvi.MsgResult
import utils.PathUtils
import utils.ShellUtils
import java.io.File

sealed class ActivityAction : BaseAction() {

    data class OnRefresh(
        val device: Device,
    ) : ActivityAction()

    data class OnScreenshot(
        val device: Device,
    ) : ActivityAction()

    data class OnSaveScreenshot(
        val device: Device,
        val msgResult: MsgResult<String?>,
    ) : ActivityAction()

    data class OnStartScrcpy(
        val device: Device,
        val msgCallback: MsgCallback,
    ) : ActivityAction()

    data object OnToggleFullClassName : ActivityAction()

    data object OnClearScreenshot : ActivityAction()
}

class ActivityViewModel : BaseViewModel<ActivityAction>() {
    private val _activity = MutableStateFlow<Activity?>(null)
    private val _screenshot = MutableStateFlow<Screenshot?>(null)
    private val _toggleFullClassName = MutableStateFlow(false)
    private val _isScrcpyRunning = MutableStateFlow(setOf<String>())

    val activity: StateFlow<Activity?> = _activity
    val screenshot: StateFlow<Screenshot?> = _screenshot
    val toggleFullClassName: StateFlow<Boolean> = _toggleFullClassName
    val isScrcpyRunning: StateFlow<Set<String>> = _isScrcpyRunning

    // 切换是否显示完整类名
    // Toggles whether to display the full class name
    private fun onToggleFullClassName() {
        _toggleFullClassName.value = !toggleFullClassName.value
    }

    // 刷新Activity
    // Refresh Activity
    private fun onRefresh(device: Device) {
        singleLaunchIO("refresh") {
            _activity.value = AdbServer.instance.getActivity(device)
        }
    }

    // 启动scrcpy
    // Start scrcpy
    private fun onStartScrcpy(device: Device, msgCallback: MsgCallback) {
        singleLaunchIO("startScrcpy") {
            if (isScrcpyRunning.value.contains(device.serialNo)) {
                msgCallback.onMsg(StringRes.locale.scrcpyRunning)
                return@singleLaunchIO
            }

            runCatching {
                msgCallback.onMsg(StringRes.locale.startScrcpyWaiting)
                _isScrcpyRunning.value += device.serialNo
                val process = ShellUtils.exec("scrcpy -s ${device.serialNo}")
                process.waitFor()
                _isScrcpyRunning.value -= device.serialNo
            }.onFailure {
                it.printStackTrace()
                _isScrcpyRunning.value -= device.serialNo
            }
        }
    }

    // 截图
    // Screenshot
    private fun onScreenshot(device: Device) {
        singleLaunchIO("screenshot") {
            _screenshot.value = AdbServer.instance.screenshot(device)
        }
    }

    // 清除截图
    // Clear screenshot
    private fun onClearScreenshot() {
        _screenshot.value = null
    }

    // 保存截图
    // save screenshot
    private fun onSaveScreenshot(
        device: Device,
        msgResult: MsgResult<String?>,
    ) {
        singleLaunchIO("onSaveScreenshot") {
            val download = PathUtils.getDownloadPath()
            if (download == null) {
                msgResult.onResult(StringRes.locale.noFoundDownloadPath, null)
                return@singleLaunchIO
            }
            val file = File(download, "${device.model}_${System.currentTimeMillis()}.png")
            val bytes = screenshot.value?.data
            if (bytes == null) {
                msgResult.onResult(StringRes.locale.screenshotCleared, download)
                return@singleLaunchIO
            }

            file.outputStream().use {
                it.write(bytes)
            }

            msgResult.onResult(StringRes.locale.screenshotSaved, download)
        }
    }

    override fun dispatch(action: ActivityAction) {
        when (action) {
            is ActivityAction.OnRefresh -> onRefresh(action.device)
            is ActivityAction.OnScreenshot -> onScreenshot(action.device)
            is ActivityAction.OnSaveScreenshot -> onSaveScreenshot(action.device, action.msgResult)
            is ActivityAction.OnStartScrcpy -> onStartScrcpy(action.device, action.msgCallback)
            is ActivityAction.OnClearScreenshot -> onClearScreenshot()
            is ActivityAction.OnToggleFullClassName -> onToggleFullClassName()
        }
    }
}