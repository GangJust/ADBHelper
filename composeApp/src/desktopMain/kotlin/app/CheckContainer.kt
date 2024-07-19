package app

import LocalDevice
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import i18n.StringRes

// 检查是否有设备连接
// Check if there is a device connected
@Composable
fun CheckContainer(
    content: @Composable () -> Unit,
) {
    if (LocalDevice.current == null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(StringRes.locale.noDeviceConnection)
        }
    } else {
        content()
    }
}