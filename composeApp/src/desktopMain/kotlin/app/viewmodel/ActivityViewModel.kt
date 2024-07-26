package app.viewmodel

import adb.AdbServer
import adb.entity.Activity
import adb.entity.Device
import adb.entity.Screenshot
import entity.AppConfig
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

    data object OnToggleFullClassName : ActivityAction()

    data object OnClearScreenshot : ActivityAction()

    data class OnScrcpyDialog(
        val show: Boolean,
    ) : ActivityAction()

    data class OnSaveScrcpyPath(
        val device: Device,
        val scrcpyPath: String,
    ) : ActivityAction()

    data class OnStartScrcpy(
        val device: Device,
        val msgCallback: MsgCallback,
    ) : ActivityAction()
}

class ActivityViewModel : BaseViewModel<ActivityAction>() {
    private val _activity = MutableStateFlow<Activity?>(null)
    private val _screenshot = MutableStateFlow<Screenshot?>(null)
    private val _toggleFullClassName = MutableStateFlow(false)
    private val _scrcpyDialog = MutableStateFlow(false)
    private val _isScrcpyRunning = MutableStateFlow(setOf<String>())

    val activity: StateFlow<Activity?> = _activity
    val screenshot: StateFlow<Screenshot?> = _screenshot
    val toggleFullClassName: StateFlow<Boolean> = _toggleFullClassName
    val scrcpyDialog: StateFlow<Boolean> = _scrcpyDialog
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

    // 显示/隐藏scrcpy路径对话框
    // Show/hide scrcpy path dialog
    private fun onScrcpyDialog(show: Boolean) {
        _scrcpyDialog.value = show
    }

    // 保存scrcpy路径
    // Save scrcpy path
    private fun onSaveScrcpyPath(device: Device, scrcpyPath: String) {
        singleLaunchIO("onSaveScrcpyPath") {
            AppConfig.write(scrcpyPath = scrcpyPath)
            // start scrcpy
            onStartScrcpy(device) { /* nothing */ }
        }
    }

    // 启动scrcpy
    // Start scrcpy
    private fun onStartScrcpy(device: Device, msgCallback: MsgCallback) {
        singleLaunchIO("startScrcpy") {
            // 检查是否已经启动scrcpy|Check if scrcpy has been started
            if (isScrcpyRunning.value.contains(device.serialNo)) {
                msgCallback.onMsg(StringRes.locale.scrcpyRunning)
                return@singleLaunchIO
            }

            // 检查scrcpy路径|Check scrcpy path
            val config = AppConfig.read()

            if (config.scrcpyPath.isEmpty()) {
                msgCallback.onMsg(StringRes.locale.noFoundScrcpyPath)
                _scrcpyDialog.value = true
                return@singleLaunchIO
            }

            // 启动scrcpy|Start scrcpy
            runCatching {
                _scrcpyDialog.value = false
                _isScrcpyRunning.value += device.serialNo

                msgCallback.onMsg(StringRes.locale.startScrcpyWaiting)

                val process = ShellUtils.exec(
                    command = arrayOf("scrcpy -s ${device.serialNo}"),
                    environment = ShellUtils.environment("PATH", config.adbPath, config.scrcpyPath),
                )

                val errMsg = process.errorReader().readText().trim()
                if (errMsg.isNotEmpty()) {
                    msgCallback.onMsg(errMsg)
                }

                process.waitFor()
                _isScrcpyRunning.value -= device.serialNo
            }.onFailure {
                msgCallback.onMsg(it.message ?: "")
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
            is ActivityAction.OnClearScreenshot -> onClearScreenshot()
            is ActivityAction.OnToggleFullClassName -> onToggleFullClassName()
            is ActivityAction.OnScrcpyDialog -> onScrcpyDialog(action.show)
            is ActivityAction.OnSaveScrcpyPath -> onSaveScrcpyPath(action.device, action.scrcpyPath)
            is ActivityAction.OnStartScrcpy -> onStartScrcpy(action.device, action.msgCallback)
        }
    }
}