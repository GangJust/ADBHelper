package io.github.adbhelper

import adbhelper.composeapp.generated.resources.Res
import adbhelper.composeapp.generated.resources.ic_activity
import adbhelper.composeapp.generated.resources.ic_app
import adbhelper.composeapp.generated.resources.ic_close
import adbhelper.composeapp.generated.resources.ic_folder
import adbhelper.composeapp.generated.resources.ic_layout
import adbhelper.composeapp.generated.resources.ic_phone
import adbhelper.composeapp.generated.resources.ic_refresh
import adbhelper.composeapp.generated.resources.ic_terminal
import adbhelper.composeapp.generated.resources.ic_timer
import adbhelper.composeapp.generated.resources.ic_wifi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import compose.common.view.CardTextField
import io.github.adbhelper.adb.entity.Device
import io.github.adbhelper.app.CheckContainer
import io.github.adbhelper.app.page.ActivityPage
import io.github.adbhelper.app.page.AppManagerPage
import io.github.adbhelper.app.page.FileManagerPage
import io.github.adbhelper.app.page.LayoutAnalysisPage
import io.github.adbhelper.app.page.ScheduleTasksPage
import io.github.adbhelper.app.page.TerminalPage
import io.github.adbhelper.common.compose.CardDialog
import io.github.adbhelper.common.compose.ExtendedMenu
import io.github.adbhelper.common.compose.ExtendedMenuItem
import io.github.adbhelper.common.compose.ExtendedMenuItemBox
import io.github.adbhelper.common.compose.Toast
import io.github.adbhelper.common.compose.rememberExtendedMenuState
import io.github.adbhelper.common.res.IconRes
import io.github.adbhelper.common.res.icons.Github
import io.github.adbhelper.compose.ActionIconButton
import io.github.adbhelper.compose.WindowTopBar
import io.github.adbhelper.i18n.StringRes
import io.github.adbhelper.window.LocalWindowScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import java.awt.Desktop
import java.awt.datatransfer.StringSelection
import java.net.URI

sealed class AppNavigator {
    @Serializable
    data object Activity : AppNavigator()

    @Serializable
    data object AppManager : AppNavigator()

    @Serializable
    data object FileManager : AppNavigator()

    @Serializable
    data object LayoutAnalysis : AppNavigator()

    @Serializable
    data object ScheduleTasks : AppNavigator()

    @Serializable
    data object Terminal : AppNavigator()
}

fun <T : Any> NavHostController.appNavigate(route: T) {
    return navigate(route) {
        popUpTo(AppNavigator.Activity) {
            inclusive = false
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

val LocalDevice = compositionLocalOf {
    Device.Empty
}

// App入口|App Entrance
@Composable
fun AdbHelperApp(
    onCloseRequest: () -> Unit,
    model: AppViewModel = composeViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

    AppContentWrapper {
        Scaffold(
            Modifier.fillMaxSize(),
        ) {
            val navController = rememberNavController()
            Row {
                // 左侧菜单|Left menu
                AppMenu(
                    model = model,
                    navController = navController,
                )

                // 右侧布局|Right Layout
                Column(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .weight(1f),
                ) {
                    // 顶部可拖拽操作栏|Top draggable operation bar
                    WindowTopBar(onCloseRequest) {
                        ActionIconButton(
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    val url = "https://github.com/GangJust/AdbHelper"
                                    runCatching {
                                        Desktop.getDesktop().browse(URI.create(url))
                                    }.onFailure {
                                        clipboard.setClipEntry(ClipEntry(StringSelection(url))) // see: https://youtrack.jetbrains.com/issue/CMP-7624
                                        withContext(Dispatchers.Main) {
                                            Toast.show(StringRes.locale.githubUrlCopied)
                                        }
                                    }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = IconRes.Github,
                                contentDescription = "github",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // 内容布局|Content layout
                    val device by model.currDevice.collectAsState()
                    CompositionLocalProvider(LocalDevice provides device) {
                        NavHost(
                            navController = navController,
                            startDestination = AppNavigator.Activity,
                        ) {
                            composable<AppNavigator.Activity> {
                                // NavHost composable will generate multiple calls, which is an expected situation. For more information, see: https://issuetracker.google.com/issues/225987040
                                CheckContainer(device) {
                                    ActivityPage()
                                }
                            }

                            composable<AppNavigator.AppManager> {
                                CheckContainer(device) {
                                    AppManagerPage()
                                }
                            }

                            composable<AppNavigator.FileManager> {
                                CheckContainer(device) {
                                    FileManagerPage()
                                }
                            }

                            composable<AppNavigator.LayoutAnalysis> {
                                CheckContainer(device) {
                                    LayoutAnalysisPage()
                                }
                            }

                            composable<AppNavigator.ScheduleTasks> {
                                CheckContainer(device) {
                                    ScheduleTasksPage()
                                }
                            }

                            composable<AppNavigator.Terminal> {
                                CheckContainer(device) {
                                    TerminalPage()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 左侧菜单|Left menu
@Composable
private fun AppMenu(
    model: AppViewModel,
    navController: NavHostController,
) {

    val menuState = rememberExtendedMenuState()
    val menus = listOf(
        Triple(Res.drawable.ic_activity, StringRes.locale.activityInfo) {
            navController.appNavigate(AppNavigator.Activity)
        },
        Triple(Res.drawable.ic_app, StringRes.locale.appManage) {
            navController.appNavigate(AppNavigator.AppManager)
        },
        Triple(Res.drawable.ic_folder, StringRes.locale.fileManage) {
            navController.appNavigate(AppNavigator.FileManager)
        },
        Triple(Res.drawable.ic_layout, StringRes.locale.layoutAnalyse) {
            navController.appNavigate(AppNavigator.LayoutAnalysis)
        },
        Triple(Res.drawable.ic_timer, StringRes.locale.scheduledTasks) {
            navController.appNavigate(AppNavigator.ScheduleTasks)
        },
        Triple(Res.drawable.ic_terminal, StringRes.locale.terminal) {
            navController.appNavigate(AppNavigator.Terminal)
        },
    )

    var selected by remember { mutableStateOf(menus.first()) }
    var deviceSelectedDialog by remember { mutableStateOf(false) }

    ExtendedMenu(
        state = menuState,
        elevation = 4.dp,
        header = {
            ExtendedMenuItemBox(
                onClick = {
                    menuState.toggle()
                },
                indication = null,
                openContent = {
                    Text(
                        text = StringRes.locale.title1,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                shrinkContent = {
                    Text(
                        text = StringRes.locale.simpleTitle,
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                },
            ) { content ->
                Box(
                    modifier = Modifier
                        .padding(top = 48.dp, bottom = 32.dp)
                        .padding(horizontal = 24.dp),
                ) {
                    content()
                }
            }
        },
        footer = {
            val device by model.currDevice.collectAsState()
            ExtendedMenuItemBox(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    deviceSelectedDialog = true
                },
                openContent = {
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth(),
                    ) {
                        Text(
                            text = device.brandModel.ifEmpty { StringRes.locale.noDeviceSelected },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.body2,
                        )
                        Text(
                            text = device.displaySerialNo.ifEmpty { StringRes.locale.notConnected },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.caption,
                        )
                    }
                },
                shrinkContent = { },
            ) { content ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_phone),
                        contentDescription = "current device.",
                    )
                    content.invoke()
                }
            }
        }
    ) {
        menus.forEach { menu ->
            ExtendedMenuItem(
                selected = selected == menu,
                icon = {
                    Icon(
                        painter = painterResource(menu.first),
                        contentDescription = menu.second,
                    )
                },
                label = {
                    Text(
                        text = menu.second,
                        style = MaterialTheme.typography.body1,
                    )
                },
                onClick = {
                    selected = menu
                    menu.third()
                }
            )
        }
    }

    if (deviceSelectedDialog) {
        DeviceSelectedDialog(
            onDismiss = { deviceSelectedDialog = false },
            model = model,
        )
    }
}

// 设备列表弹窗|Device list Dialog
@Composable
private fun DeviceSelectedDialog(
    onDismiss: () -> Unit,
    model: AppViewModel,
) {
    val windowScope = LocalWindowScope.current
    val isWaiting by model.isWaiting.collectAsState()
    var wifiToggle by remember { mutableStateOf(false) }

    CardDialog(
        onDismiss = { /* 不响应|unresponsive */ },
    ) {
        Column {
            windowScope.WindowDraggableArea {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(56.dp),
                ) {
                    Text(
                        text = StringRes.locale.deviceList,
                        modifier = Modifier.padding(start = 24.dp),
                    )
                    Spacer(Modifier.weight(1f))
                    Row {
                        ActionIconButton(
                            enabled = !isWaiting,
                            onClick = {
                                wifiToggle = !wifiToggle
                            },
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_wifi),
                                contentDescription = "connect",
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        AnimatedVisibility(
                            visible = !wifiToggle
                        ) {
                            ActionIconButton(
                                enabled = !isWaiting,
                                onClick = {
                                    model.dispatch(AppAction.Devices)
                                },
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_refresh),
                                    contentDescription = "refresh",
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                        ActionIconButton(
                            onClick = onDismiss,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_close),
                                contentDescription = "close",
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }

            AnimatedContent(
                targetState = wifiToggle,
            ) { wifi ->
                if (wifi) {
                    DeviceWifiContent(
                        onConnectSuccess = { wifiToggle = false },
                        model = model,
                    )
                } else {
                    DeviceListContent(
                        onSelected = { onDismiss() },
                        model = model,
                    )
                }
            }
        }
    }
}

// 设备列表内容|Device list content
@Composable
private fun DeviceListContent(
    onSelected: (device: Device) -> Unit,
    model: AppViewModel,
) {
    val isWaiting by model.isWaiting.collectAsState()
    val devices by model.devices.collectAsState()

    LaunchedEffect(Unit) {
        model.dispatch(AppAction.Devices)
    }

    if (isWaiting) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(28.dp),
        ) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.padding(4.dp))
            Text(
                text = StringRes.locale.loadDeviceWaiting,
                style = MaterialTheme.typography.body2,
            )
        }
        return
    }

    LazyColumn {
        if (devices.isEmpty()) {
            item {
                Text(
                    text = StringRes.locale.noDeviceConnection,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(24.dp),
                )
            }
        }

        items(devices) { device ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    model.dispatch(AppAction.CurrentDevice(device))
                    onSelected(device)
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_phone),
                    contentDescription = "Device",
                    modifier = Modifier.padding(start = 24.dp),
                )
                Text(
                    text = device.brandModelSerialNo,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = device.state,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(end = 24.dp),
                )
            }
        }
    }
}

// 设备wifi连接|WiFi connection
@Composable
private fun DeviceWifiContent(
    onConnectSuccess: () -> Unit,
    model: AppViewModel,
) {
    val isWaiting by model.isWaiting.collectAsState()
    val ipAndPort by model.ipAndPort.collectAsState(Dispatchers.Main)
    val onConnect = {
        val callback = { message: String ->
            Toast.show(message.trim())
            if (message.contains("connected")) {
                onConnectSuccess()
            }
        }
        model.dispatch(AppAction.Connect(ipAndPort, callback))
    }

    Column {
        CardTextField(
            value = ipAndPort,
            placeholder = StringRes.locale.ipAndPortPlaceholder,
            backgroundColor = Color.Transparent,
            singleLine = true,
            onValueChange = {
                model.dispatch(AppAction.ChangeIpAndPort(it))
            },
            modifier = Modifier
                .onKeyEvent {
                    if (it.key == Key.Enter) {
                        onConnect.invoke()
                        true
                    } else {
                        false
                    }
                }
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
        )
        Row {
            Spacer(Modifier.weight(1f))
            TextButton(
                enabled = !isWaiting,
                onClick = onConnect,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            ) {
                if (isWaiting) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    Text(StringRes.locale.connect)
                }
            }
        }
    }
}