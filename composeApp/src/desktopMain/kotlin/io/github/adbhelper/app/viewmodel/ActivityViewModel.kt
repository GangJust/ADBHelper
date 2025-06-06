package io.github.adbhelper.app.viewmodel

import io.github.adbhelper.adb.AdbServer
import io.github.adbhelper.adb.entity.Activity
import io.github.adbhelper.adb.entity.Device
import io.github.adbhelper.adb.entity.Screenshot
import io.github.adbhelper.entity.AppConfig
import io.github.adbhelper.i18n.StringRes
import io.github.adbhelper.mvi.BaseAction
import io.github.adbhelper.mvi.BaseMVI
import io.github.adbhelper.mvi.MsgCallback
import io.github.adbhelper.mvi.MsgResult
import io.github.adbhelper.utils.PathUtils
import io.github.adbhelper.utils.ShellUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

sealed class ActivityAction : BaseAction() {

    data class Refresh(
        val device: Device,
    ) : ActivityAction()

    data class Screenshot(
        val device: Device,
    ) : ActivityAction()

    data class SaveScreenshot(
        val device: Device,
        val msgResult: MsgResult<String?>,
    ) : ActivityAction()

    data object ToggleFullClassName : ActivityAction()

    data object ClearScreenshot : ActivityAction()

    data class ScrcpyDialog(
        val show: Boolean,
    ) : ActivityAction()

    data class SaveScrcpyPath(
        val device: Device,
        val scrcpyPath: String,
    ) : ActivityAction()

    data class StartScrcpy(
        val device: Device,
        val msgCallback: MsgCallback,
    ) : ActivityAction()
}

class ActivityViewModel() : BaseMVI<ActivityAction>() {
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
    private fun handleToggleFullClassName() {
        _toggleFullClassName.value = !toggleFullClassName.value
    }

    // 刷新Activity
    // Refresh Activity
    private fun handleRefresh(device: Device) {
        singleLaunchIO("refresh") {
            _activity.value = AdbServer.instance.getActivity(device)
        }
    }

    // 显示/隐藏scrcpy路径对话框
    // Show/hide scrcpy path dialog
    private fun handleScrcpyDialog(show: Boolean) {
        _scrcpyDialog.value = show
    }

    // 保存scrcpy路径
    // Save scrcpy path
    private fun handleSaveScrcpyPath(device: Device, scrcpyPath: String) {
        singleLaunchIO("onSaveScrcpyPath") {
            AppConfig.write(scrcpyPath = scrcpyPath)
            // start scrcpy
            handleStartScrcpy(device) { /* nothing */ }
        }
    }

    // 启动scrcpy
    // Start scrcpy
    private fun handleStartScrcpy(device: Device, msgCallback: MsgCallback) {
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
    private fun handleScreenshot(device: Device) {
        singleLaunchIO("screenshot") {
            _screenshot.value = AdbServer.instance.screenshot(device)
        }
    }

    // 清除截图
    // Clear screenshot
    private fun handleClearScreenshot() {
        _screenshot.value = null
    }

    // 保存截图
    // save screenshot
    private fun handleSaveScreenshot(
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
            is ActivityAction.Refresh -> handleRefresh(action.device)
            is ActivityAction.Screenshot -> handleScreenshot(action.device)
            is ActivityAction.SaveScreenshot -> handleSaveScreenshot(action.device, action.msgResult)
            is ActivityAction.ClearScreenshot -> handleClearScreenshot()
            is ActivityAction.ToggleFullClassName -> handleToggleFullClassName()
            is ActivityAction.ScrcpyDialog -> handleScrcpyDialog(action.show)
            is ActivityAction.SaveScrcpyPath -> handleSaveScrcpyPath(action.device, action.scrcpyPath)
            is ActivityAction.StartScrcpy -> handleStartScrcpy(action.device, action.msgCallback)
        }
    }
}