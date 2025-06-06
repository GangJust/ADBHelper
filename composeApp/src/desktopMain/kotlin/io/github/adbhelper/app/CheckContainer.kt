package io.github.adbhelper.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.adbhelper.adb.entity.Device
import io.github.adbhelper.i18n.StringRes

// 检查是否有设备连接
// Check if there is a device connected
@Composable
fun CheckContainer(
    device: Device,
    content: @Composable () -> Unit,
) {
    if (device == Device.Empty) {
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