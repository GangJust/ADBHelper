import adb.AdbServer
import adb.entity.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mvi.BaseAction
import mvi.BaseViewModel

sealed class AppAction : BaseAction() {
    data class Waiting(val waiting: Boolean) : AppAction()
    data object GetDevices : AppAction()
    data class SetCurrentDevice(val device: Device) : AppAction()
    data class OnChangeIpAndPort(val ipAndPort: String) : AppAction()
    data class Connect(val ip: String, val callback: (String) -> Unit) : AppAction()
}

class AppViewModel : BaseViewModel<AppAction>() {
    private val _isWaiting = MutableStateFlow(false)
    private val _devices = MutableStateFlow(listOf<Device>())
    private val _currentDevice = MutableStateFlow<Device?>(null)
    private val _ipAndPort = MutableStateFlow("")

    val isWaiting: StateFlow<Boolean> = _isWaiting
    val devices: StateFlow<List<Device>> = _devices
    val currentDevice: StateFlow<Device?> = _currentDevice
    val ipAndPort: StateFlow<String> = _ipAndPort

    private fun getDevices() {
        singleLaunchIO("getDevices") {
            waiting(true)
            _devices.value = AdbServer.instance.getDevices()
            waiting(false)
        }
    }

    private fun setCurrentDevice(device: Device) {
        _currentDevice.value = device
    }

    private fun setIpAndPort(ipAndPort: String) {
        _ipAndPort.value = ipAndPort
    }

    private fun connect(ip: String, callback: (String) -> Unit) {
        singleLaunchIO("connect") {
            waiting(true)
            val msg = AdbServer.instance.connect(ip)
            callback.invoke(msg)
            waiting(false)
        }
    }

    private fun waiting(waiting: Boolean) {
        _isWaiting.value = waiting
    }

    override fun dispatch(action: AppAction) {
        when (action) {
            is AppAction.GetDevices -> getDevices()
            is AppAction.SetCurrentDevice -> setCurrentDevice(action.device)
            is AppAction.Connect -> connect(action.ip, action.callback)
            is AppAction.Waiting -> waiting(action.waiting)
            is AppAction.OnChangeIpAndPort -> setIpAndPort(action.ipAndPort)
        }
    }
}