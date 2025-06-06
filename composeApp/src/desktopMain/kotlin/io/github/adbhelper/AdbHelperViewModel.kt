package io.github.adbhelper

import io.github.adbhelper.adb.AdbServer
import io.github.adbhelper.adb.entity.Device
import io.github.adbhelper.mvi.BaseAction
import io.github.adbhelper.mvi.BaseMVI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class AppAction : BaseAction() {
    data class Waiting(val waiting: Boolean) : AppAction()
    data object Devices : AppAction()
    data class CurrentDevice(val device: Device) : AppAction()
    data class ChangeIpAndPort(val ipAndPort: String) : AppAction()
    data class Connect(val ip: String, val callback: (String) -> Unit) : AppAction()
}

class AppViewModel() : BaseMVI<AppAction>() {
    private val _isWaiting = MutableStateFlow(false)
    private val _devices = MutableStateFlow(listOf<Device>())
    private val _currDevice = MutableStateFlow(Device.Empty)
    private val _ipAndPort = MutableStateFlow("")

    val isWaiting = _isWaiting.asStateFlow()
    val devices = _devices.asStateFlow()
    val currDevice = _currDevice.asStateFlow()
    val ipAndPort = _ipAndPort.asStateFlow()

    private fun handleGetDevices() {
        singleLaunchIO("handleGetDevices") {
            handleWaiting(true)
            _devices.emit(AdbServer.instance.getDevices())
            handleWaiting(false)
        }
    }

    private fun handleSetCurrentDevice(device: Device) {
        _currDevice.update { device }
    }

    private fun handleSetIpAndPort(ipAndPort: String) {
        _ipAndPort.value = ipAndPort
    }

    private fun handleConnect(ip: String, callback: (String) -> Unit) {
        singleLaunchIO("handleConnect") {
            handleWaiting(true)
            val msg = AdbServer.instance.connect(ip)
            callback.invoke(msg)
            handleWaiting(false)
        }
    }

    private fun handleWaiting(waiting: Boolean) {
        _isWaiting.value = waiting
    }

    override fun dispatch(action: AppAction) {
        when (action) {
            is AppAction.Devices -> handleGetDevices()
            is AppAction.CurrentDevice -> handleSetCurrentDevice(action.device)
            is AppAction.Connect -> handleConnect(action.ip, action.callback)
            is AppAction.Waiting -> handleWaiting(action.waiting)
            is AppAction.ChangeIpAndPort -> handleSetIpAndPort(action.ipAndPort)
        }
    }
}