import adb.entity.Device
import adbhelper.composeapp.generated.resources.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.CheckContainer
import app.page.*
import app.viewmodel.*
import common.res.IconRes
import common.res.icons.Github
import common.compose.*
import compose.ActionIconButton
import compose.WindowTopBar
import compose.common.view.CardTextField
import i18n.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import window.LocalWindowScope
import java.awt.Desktop
import java.net.URI

class Navigator {
    private val _current = mutableStateOf(Page.Activity)

    val current
        get() = _current.value

    fun toActivityPage() {
        _current.value = Page.Activity
    }

    fun toAppPage() {
        _current.value = Page.App
    }

    fun toFilePage() {
        _current.value = Page.File
    }

    fun toLayoutPage() {
        _current.value = Page.Layout
    }

    fun toSchedulePage() {
        _current.value = Page.Schedule
    }

    fun toTerminalPage() {
        _current.value = Page.Terminal
    }

    enum class Page {
        Activity,
        App,
        File,
        Layout,
        Schedule,
        Terminal,
    }
}

val LocalNavigator = compositionLocalOf<Navigator> {
    error("No Navigator provided")
}

val LocalDevice = compositionLocalOf<Device?> {
    error("No Device provided")
}

// App入口|App Entrance
@Composable
fun AdbHelperApp(
    onCloseRequest: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    AppContentWrapper {
        CompositionLocalProvider(LocalNavigator provides Navigator()) {
            Scaffold(
                Modifier.fillMaxSize(),
            ) {
                Row {
                    // 左侧菜单|Left menu
                    AppMenu()

                    // 右侧布局|Right Layout
                    Column(
                        modifier = Modifier.padding(start = 4.dp),
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
                                            clipboardManager.setText(AnnotatedString(url))
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
                        val device by viewModel<AppViewModel>().currentDevice.collectAsState()
                        CompositionLocalProvider(LocalDevice provides device) {
                            CheckContainer {
                                AppContent()
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
private fun AppMenu() {
    val viewModel: AppViewModel = viewModel()
    val navigator = LocalNavigator.current

    val menuState = rememberExtendedMenuState()
    val menus = listOf(
        Triple(Res.drawable.ic_activity, StringRes.locale.activityInfo) {
            navigator.toActivityPage()
        },
        Triple(Res.drawable.ic_app, StringRes.locale.appManage) {
            navigator.toAppPage()
        },
        Triple(Res.drawable.ic_folder, StringRes.locale.fileManage) {
            navigator.toFilePage()
        },
        Triple(Res.drawable.ic_layout, StringRes.locale.layoutAnalyse) {
            navigator.toLayoutPage()
        },
        Triple(Res.drawable.ic_timer, StringRes.locale.scheduledTask) {
            navigator.toSchedulePage()
        },
        Triple(Res.drawable.ic_terminal, StringRes.locale.simpleTerminal) {
            navigator.toTerminalPage()
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
            val currentDevice by viewModel.currentDevice.collectAsState()
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
                            text = currentDevice?.brandModel ?: StringRes.locale.noDeviceSelected,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.body2,
                        )
                        Text(
                            text = currentDevice?.displaySerialNo ?: StringRes.locale.notConnected,
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
                        contentDescription = "Current devic",
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
            onDismiss = { deviceSelectedDialog = false }
        )
    }
}

// 右侧布局|Right Content
@Composable
private fun AppContent() {
    val navigator = LocalNavigator.current
    val device = LocalDevice.current!!

    val activityViewModel = viewModel<ActivityViewModel>()
    val appListViewModel = viewModel<AppListViewModel>()
    val fileListViewModel = viewModel<FileListViewModel>()

    LaunchedEffect(device) {
        activityViewModel.dispatch(ActivityAction.OnRefresh(device))
        appListViewModel.dispatch(AppListAction.GetAppList(device))
        fileListViewModel.dispatch(FileListAction.GetFileList(device))
    }

    when (navigator.current) {
        Navigator.Page.Activity -> ActivityPage()
        Navigator.Page.App -> AppListPage()
        Navigator.Page.File -> FileListPage()
        Navigator.Page.Layout -> LayoutPage()
        Navigator.Page.Schedule -> SchedulePage()
        Navigator.Page.Terminal -> TerminalPage()
    }
}

// 设备列表弹窗|Device list Dialog
@Composable
private fun DeviceSelectedDialog(
    onDismiss: () -> Unit,
) {
    val windowScope = LocalWindowScope.current
    val viewModel: AppViewModel = viewModel()
    val isWaiting by viewModel.isWaiting.collectAsState()
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
                                    viewModel.dispatch(AppAction.GetDevices)
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
                    DeviceWifiContent {
                        wifiToggle = false
                    }
                } else {
                    DeviceListContent {
                        onDismiss()
                    }
                }
            }
        }
    }
}

// 设备列表内容|Device list content
@Composable
private fun DeviceListContent(
    onSelected: (device: Device) -> Unit,
) {
    val viewModel: AppViewModel = viewModel()
    val isWaiting by viewModel.isWaiting.collectAsState()
    val devices by viewModel.devices.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.dispatch(AppAction.GetDevices)
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
                    viewModel.dispatch(AppAction.SetCurrentDevice(device))
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
) {
    val viewModel: AppViewModel = viewModel()
    val isWaiting by viewModel.isWaiting.collectAsState()
    val ipAndPort by viewModel.ipAndPort.collectAsState(Dispatchers.Main)
    val onConnect = {
        val callback = { message: String ->
            Toast.show(message.trim())
            if (message.contains("connected")) {
                onConnectSuccess()
            }
        }
        viewModel.dispatch(AppAction.Connect(ipAndPort, callback))
    }

    Column {
        CardTextField(
            value = ipAndPort,
            placeholder = StringRes.locale.ipAndPortPlaceholder,
            backgroundColor = Color.Transparent,
            singleLine = true,
            onValueChange = {
                viewModel.dispatch(AppAction.OnChangeIpAndPort(it))
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