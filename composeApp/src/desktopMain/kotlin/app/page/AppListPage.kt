package app.page

import LocalDevice
import adb.entity.AppDesc
import adbhelper.composeapp.generated.resources.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.viewmodel.AppListAction
import app.viewmodel.AppListViewModel
import common.compose.*
import compose.ActionIconButton
import compose.DropContainer
import compose.common.view.CardTextField
import i18n.StringRes
import kotlinx.coroutines.Dispatchers
import mvi.MsgCallback
import mvi.MsgResult
import org.jetbrains.compose.resources.painterResource
import utils.PathUtils
import window.LocalWindowScope
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants

@Composable
fun AppListPage() {
    Scaffold(topBar = {
        TopBar()
    }, floatingActionButton = {
        InstallFloatButton()
    }) {
        AppList()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopBar() {
    val viewModel: AppListViewModel = viewModel()
    val device = LocalDevice.current!!

    val isWaiting by viewModel.isWaiting.collectAsState()
    val currentTabIndex by viewModel.currentTabIndex.collectAsState()
    val searchText by viewModel.searchText.collectAsState(Dispatchers.Main)

    var reloadDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(vertical = 4.dp),
    ) {
        Box {
            val allAppList by viewModel.allAppList.collectAsState()
            TabItem(
                index = 0,
                title = String.format(StringRes.locale.allApp, allAppList.size),
                selected = currentTabIndex == 0,
                onSelect = {
                    viewModel.dispatch(AppListAction.SelectedTab(it))
                },
            )
        }
        Box {
            val systemAppList by viewModel.systemAppList.collectAsState()
            TabItem(
                index = 1,
                title = String.format(StringRes.locale.systemApp, systemAppList.size),
                selected = currentTabIndex == 1,
                onSelect = {
                    viewModel.dispatch(AppListAction.SelectedTab(it))
                },
            )
        }
        Box {
            val userAppList by viewModel.userAppList.collectAsState()
            TabItem(
                index = 2,
                title = String.format(StringRes.locale.userApp, userAppList.size),
                selected = currentTabIndex == 2,
                onSelect = {
                    viewModel.dispatch(AppListAction.SelectedTab(it))
                },
            )
        }

        // 是否具有关键字|whether it has keywords
        if (searchText.isNotBlank()) {
            Box {
                val searchAppList by viewModel.searchAppList.collectAsState()
                TabItem(
                    index = 3,
                    title = String.format(StringRes.locale.searchApp, searchAppList.size),
                    selected = currentTabIndex == 3,
                    onSelect = {
                        viewModel.dispatch(AppListAction.SelectedTab(it))
                    },
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // 右侧操作|right-hand side operation
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CardTextField(
                value = searchText,
                placeholder = StringRes.locale.searchPlaceholder,
                backgroundColor = Color.Transparent,
                singleLine = true,
                modifier = Modifier
                    .width(160.dp)
                    .padding(horizontal = 4.dp),
                onValueChange = {
                    viewModel.dispatch(AppListAction.UpdateSearchText(it))
                },
            )

            ActionIconButton(
                onClick = {

                },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                )
            }

            ActionIconButton(
                onClick = {
                    if (isWaiting) {
                        Toast.show(StringRes.locale.loadAppsWaiting)
                        return@ActionIconButton
                    }
                    viewModel.dispatch(AppListAction.RefreshAppList(device))
                },
                modifier = Modifier.onClick(
                    matcher = PointerMatcher.mouse(PointerButton.Secondary)
                ) {
                    reloadDialog = true
                },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_refresh),
                    contentDescription = "Refresh",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }

    // 重新加载弹窗|reload dialog
    if (reloadDialog) {
        CardMessageDialog(
            onDismiss = { /* 不响应|unresponsive */ },
            title = StringRes.locale.tipsTitle,
            message = StringRes.locale.reloadAppList,
            cancel = StringRes.locale.cancel,
            confirm = StringRes.locale.confirm,
            onCancel = { reloadDialog = false },
            onConfirm = {
                reloadDialog = false
                if (isWaiting) {
                    Toast.show(StringRes.locale.loadAppsWaiting)
                    return@CardMessageDialog
                }
                viewModel.dispatch(AppListAction.RefreshAppList(device, true))
            }
        )
    }
}

@Composable
private fun InstallFloatButton() {
    val windowScope = LocalWindowScope.current
    var showInstallApkDialog by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = {
            showInstallApkDialog = true
        },
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_apk_install),
            contentDescription = "install"
        )
    }

    if (showInstallApkDialog) {
        windowScope.WindowDraggableArea {
            InstallDialog(onDismiss = {
                showInstallApkDialog = false
            })
        }
    }
}

@Composable
private fun InstallDialog(
    onDismiss: () -> Unit
) {
    val windowScope = LocalWindowScope.current
    val device = LocalDevice.current!!
    val viewModel: AppListViewModel = viewModel()
    var isClosed by remember { mutableStateOf(true) }
    var installMessage by remember { mutableStateOf(StringRes.locale.dropApk) }
    // var isDrag by remember { mutableStateOf(false) }
    CardDialog(
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier,
        ) {
            windowScope.WindowDraggableArea {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(56.dp),
                ) {
                    Text(
                        text = StringRes.locale.installTitle,
                        modifier = Modifier.padding(start = 24.dp),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        enabled = isClosed,
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
            Box(
                modifier = Modifier.padding(24.dp)
            ) {
                DropContainer(window = windowScope.window,
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(280.dp).clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colors.secondaryVariant),
                    dropListener = {
                        onDragEnter {
                            // isDrag = true
                            acceptDrag(DnDConstants.ACTION_REFERENCE)
                            val dataFlavor = transferable.transferDataFlavors.firstOrNull {
                                it == DataFlavor.javaFileListFlavor
                            }
                            val files = transferable.getTransferData(dataFlavor) as? List<*> ?: return@onDragEnter
                            val apkPath = files.first().toString()
                            if (apkPath.endsWith(".apk") || apkPath.endsWith(".apex")) {
                                installMessage = StringRes.locale.dropApkEnter
                            } else {
                                installMessage = StringRes.locale.dropApkFair
                                rejectDrag()
                            }
                        }
                        onDrop {
                            // isDrag = false
                            acceptDrop(DnDConstants.ACTION_REFERENCE)
                            val dataFlavor = transferable.transferDataFlavors.firstOrNull {
                                it == DataFlavor.javaFileListFlavor
                            }
                            val files = transferable.getTransferData(dataFlavor) as? List<*> ?: return@onDrop
                            val apkPath = files.first().toString()

                            isClosed = false
                            installMessage = String.format(StringRes.locale.installing, apkPath)
                            val msgCallback = MsgCallback { msg: String ->
                                isClosed = true
                                installMessage = msg
                                viewModel.dispatch(AppListAction.RefreshAppList(device))
                            }
                            viewModel.dispatch(AppListAction.InstallApk(device, apkPath, msgCallback))

                            dropComplete(true)
                        }
                        onDragExit {
                            // isDrag = false
                            installMessage = StringRes.locale.dropApk
                        }
                    }) {
                    SelectionText(
                        value = installMessage,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    )
                }
            }
        }
    }
}

// 应用列表|App List
@Composable
private fun AppList() {
    val viewModel: AppListViewModel = viewModel()
    val device = LocalDevice.current!!

    var isShowOptionDialog by remember { mutableStateOf(false) }
    var optionMenu by remember { mutableStateOf("") }
    var optionDesc by remember { mutableStateOf<AppDesc?>(null) }

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val currentTabIndex by viewModel.currentTabIndex.collectAsState()
        val currentListState = viewModel.currentListState(currentTabIndex)
        val currentAppList by viewModel.currentAppList(currentTabIndex).collectAsState()

        LazyColumn(
            state = currentListState,
        ) {
            items(currentAppList) { it ->
                AppListItem(
                    desc = it,
                    onRefresh = { app ->
                        viewModel.dispatch(AppListAction.RefreshAppItem(device, app))
                    },
                    onMenuClick = { menu, app ->
                        isShowOptionDialog = true
                        optionMenu = menu
                        optionDesc = app
                    },
                )
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(currentListState),
            modifier = Modifier.padding(horizontal = 4.dp).align(Alignment.CenterEnd)
        )
    }

    if (isShowOptionDialog) {
        OperateDialogs(
            onDismiss = {
                isShowOptionDialog = false
            },
            menu = optionMenu,
            desc = optionDesc!!,
        )
    }
}

@Composable
private fun AppListItem(
    desc: AppDesc,
    onRefresh: (desc: AppDesc) -> Unit,
    onMenuClick: (menu: String, desc: AppDesc) -> Unit,
) {
    val optionMenus = listOf(
        Pair(StringRes.locale.exportApk, true),
        Pair(StringRes.locale.launchApp, true),
        Pair(StringRes.locale.killApp, true),
        Pair(StringRes.locale.clearApp, true),
        Pair(StringRes.locale.uninstallApp, true),
    )

    Card(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            AppItemAttribute(
                label = StringRes.locale.appPackageName,
                value = desc.packageName,
            )
            AppItemAttribute(
                label = StringRes.locale.appVersionName,
                value = desc.versionName,
            )
            AppItemAttribute(
                label = StringRes.locale.appVersionCode,
                value = desc.versionCode,
            )
            AppItemAttribute(
                label = StringRes.locale.appLaunchActivity,
                value = desc.launcherActivity.ifEmpty { StringRes.locale.nothing },
            )
            AppItemAttribute(
                label = StringRes.locale.appPrimaryAbi,
                value = desc.primaryCpuAbi.ifEmpty { StringRes.locale.nothing },
            )
            AppItemAttribute(
                label = StringRes.locale.appTargetSdk,
                value = desc.targetSdk,
            )
            AppItemAttribute(
                label = StringRes.locale.appMinSdk,
                value = desc.minSdk,
            )
            AppItemAttribute(
                label = StringRes.locale.appSignVersion,
                value = desc.signVersion,
            )
            AppItemAttribute(
                label = StringRes.locale.appIsSystem,
                value = if (desc.isSystem) StringRes.locale.yes else StringRes.locale.no,
            )
            AppItemAttribute(
                label = StringRes.locale.appSize,
                value = desc.size,
            )
            AppItemAttribute(
                label = StringRes.locale.appInstallDate,
                value = desc.firstInstallTime,
            )
            AppItemAttribute(
                label = StringRes.locale.appUpdateDate,
                value = desc.lastUpdateTime,
            )
            AppItemAttribute(
                label = StringRes.locale.appDataDir,
                value = desc.dataDir,
            )
            AppItemAttribute(
                label = StringRes.locale.appExtDataDir,
                value = desc.externalDataDir,
            )
            AppItemAttribute(
                label = StringRes.locale.appInstallPath,
                value = desc.installPath,
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
            ) {
                // refresh
                IconButton(
                    onClick = {
                        onRefresh(desc)
                    },
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_refresh),
                        contentDescription = "Refresh",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // menu
                Box {
                    var expanded by remember { mutableStateOf(false) }

                    IconButton(
                        onClick = {
                            expanded = true
                        },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_more_vert),
                            contentDescription = "More",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    CursorDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        },
                    ) {
                        optionMenus.forEach { menu ->
                            DropdownMenuItem(
                                enabled = menu.second,
                                onClick = {
                                    expanded = false
                                    onMenuClick(menu.first, desc)
                                },
                            ) {
                                Text(
                                    text = menu.first,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppItemAttribute(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.widthIn(
                min = if (StringRes.language == "zh") 80.dp else 120.dp
            ),
        )
        Spacer(modifier = Modifier.padding(horizontal = 12.dp))
        SelectionText(
            value = value,
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
    }
}

// 操作|operate
@Composable
private fun OperateDialogs(
    onDismiss: () -> Unit,
    menu: String,
    desc: AppDesc,
) {
    val device = LocalDevice.current!!
    val viewModel: AppListViewModel = viewModel()

    var isLoading by remember { mutableStateOf(false) }
    when (menu) {
        StringRes.locale.uninstallApp -> {
            CardMessageDialog(
                onDismiss = onDismiss,
                isLoading = isLoading,
                title = StringRes.locale.tipsTitle,
                message = StringRes.locale.uninstallMessage,
                cancel = StringRes.locale.cancel,
                confirm = StringRes.locale.confirm,
                onCancel = onDismiss,
                onConfirm = {
                    isLoading = true
                    val msgCallback = MsgCallback { msg: String ->
                        isLoading = false
                        onDismiss.invoke()
                        Toast.show(msg)
                        viewModel.dispatch(AppListAction.RefreshAppList(device))
                    }
                    viewModel.dispatch(AppListAction.UninstallApp(device, desc, msgCallback))
                },
            )
        }

        StringRes.locale.clearApp -> {
            CardMessageDialog(
                onDismiss = onDismiss,
                isLoading = isLoading,
                title = StringRes.locale.tipsTitle,
                message = StringRes.locale.clearAppMessage,
                cancel = StringRes.locale.cancel,
                confirm = StringRes.locale.confirm,
                onCancel = onDismiss,
                onConfirm = {
                    isLoading = true
                    val msgCallback = MsgCallback { msg: String ->
                        isLoading = false
                        onDismiss.invoke()
                        Toast.show(msg)
                    }
                    viewModel.dispatch(AppListAction.ClearDataApp(device, desc, msgCallback))
                },
            )
        }

        StringRes.locale.killApp -> {
            CardMessageDialog(
                onDismiss = onDismiss,
                isLoading = isLoading,
                title = StringRes.locale.tipsTitle,
                message = StringRes.locale.killAppMessage,
                cancel = StringRes.locale.cancel,
                confirm = StringRes.locale.confirm,
                onCancel = onDismiss,
                onConfirm = {
                    isLoading = true
                    val msgCallback = MsgCallback { msg: String ->
                        isLoading = false
                        onDismiss.invoke()
                        Toast.show(msg)
                    }
                    viewModel.dispatch(AppListAction.KillProcessApp(device, desc, msgCallback))
                },
            )
        }

        StringRes.locale.launchApp -> {
            Toast.show(StringRes.locale.launchAppMessage)
            val msgCallback = MsgCallback { msg: String ->
                Toast.show(msg)
            }
            viewModel.dispatch(AppListAction.LaunchActivity(device, desc, msgCallback))
        }

        StringRes.locale.exportApk -> {
            CardMessageDialog(
                onDismiss = { /* 不响应|unresponsive */ },
                isLoading = isLoading,
                title = StringRes.locale.tipsTitle,
                message = StringRes.locale.exportApkMessage,
                cancel = StringRes.locale.cancel,
                confirm = StringRes.locale.confirm,
                onCancel = onDismiss,
                onConfirm = {
                    isLoading = true
                    val msgCallback = MsgResult { msg: String, path: String? ->
                        isLoading = false
                        Toast.show(msg)
                        onDismiss.invoke()
                        if (!msg.contains("err") && !msg.contains("fail")) {
                            PathUtils.openDir(path)
                        }
                    }
                    viewModel.dispatch(AppListAction.ExportApp(device, desc, msgCallback))
                },
            )
        }
    }
}
