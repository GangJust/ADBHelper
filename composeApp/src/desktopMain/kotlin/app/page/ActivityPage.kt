package app.page

import LocalDevice
import adbhelper.composeapp.generated.resources.Res
import adbhelper.composeapp.generated.resources.ic_handoff
import adbhelper.composeapp.generated.resources.ic_refresh
import adbhelper.composeapp.generated.resources.ic_screenshot
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import androidx.lifecycle.viewmodel.compose.viewModel
import app.viewmodel.ActivityAction
import app.viewmodel.ActivityViewModel
import common.compose.*
import common.res.IconRes
import compose.common.res.icons.Scrcpy
import compose.common.view.CardTextField
import i18n.StringRes
import mvi.MsgCallback
import mvi.MsgResult
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.Image
import utils.PathUtils

@Composable
fun ActivityPage() {
    val viewModel: ActivityViewModel = viewModel()

    Scaffold(floatingActionButton = {
        FloatButtons()
    }) {
        val activity by viewModel.activity.collectAsState()
        val handoff by viewModel.toggleFullClassName.collectAsState()

        Column(
            modifier = Modifier.padding(vertical = 12.dp),
        ) {
            SingleItem(StringRes.locale.currPackageName, activity?.packageName)
            SingleItem(StringRes.locale.currProcessName, activity?.processName)
            SingleItem(StringRes.locale.launchActivityName, activity?.fullLaunchActivity(handoff))
            SingleItem(StringRes.locale.currActivityName, activity?.fullResumedActivity(handoff))
            SingleItem(StringRes.locale.lastActivityName, activity?.fullLastActivity(handoff))
            MultiItem(StringRes.locale.stackActivities, activity?.fullStackActivities(handoff))
        }
    }

    ScreenshotDialogWindow()

    ScrcpyConfigDialog()
}

@Composable
private fun SingleItem(
    text: String,
    content: String?,
) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(end = 12.dp),
            )
            SelectionText(
                value = content ?: StringRes.locale.nothing,
                singleLine = true,
            )
        }
    }
}

@Composable
private fun MultiItem(
    text: String,
    contents: List<String>?,
) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(end = 12.dp),
            )
            LazyColumn {
                items(contents ?: emptyList()) { content ->
                    SelectionText(
                        value = content,
                        singleLine = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatButtons() {
    val viewModel: ActivityViewModel = viewModel()
    val device = LocalDevice.current!!

    Column {
        FloatingActionButton(
            onClick = {
                val callback = MsgCallback {
                    Toast.show(it)
                }
                viewModel.dispatch(ActivityAction.OnStartScrcpy(device, callback))
            },
        ) {
            Icon(
                imageVector = IconRes.Scrcpy,
                contentDescription = "scrcpy"
            )
        }
        Spacer(Modifier.padding(vertical = 4.dp))
        FloatingActionButton(
            onClick = {
                Toast.show(StringRes.locale.screenshotWaiting)
                viewModel.dispatch(ActivityAction.OnScreenshot(device))
            },
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_screenshot),
                contentDescription = "screenshot"
            )
        }
        Spacer(Modifier.padding(vertical = 4.dp))
        FloatingActionButton(
            onClick = {
                viewModel.dispatch(ActivityAction.OnToggleFullClassName)
            },
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_handoff),
                contentDescription = "toggle"
            )
        }
        Spacer(Modifier.padding(vertical = 4.dp))
        FloatingActionButton(
            onClick = {
                viewModel.dispatch(ActivityAction.OnRefresh(device))
            },
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_refresh),
                contentDescription = "refresh"
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScreenshotDialogWindow() {
    val viewModel: ActivityViewModel = viewModel()
    val device = LocalDevice.current!!
    val screenshot by viewModel.screenshot.collectAsState()

    screenshot ?: return // 无截屏数据，不显示|No screenshot data

    val width = screenshot!!.width.toInt()
    val height = screenshot!!.height.toInt()
    val ratio = if (width > height) { // 按固定值 430 计算比例|430 Calculation Ratio
        height / 430.0
    } else {
        width / 430.0
    }

    val subToast = rememberToast()
    val encoded = Image.makeFromEncoded(screenshot!!.data)
    val onCloseRequest = {
        runCatching { encoded.close() } //
        viewModel.dispatch(ActivityAction.OnClearScreenshot)
    }
    DialogWindow(
        title = device.brandModelSerialNo,
        resizable = false,
        onCloseRequest = onCloseRequest,
        state = rememberDialogState(
            width = (width / ratio).toInt().dp,
            height = (height / ratio).toInt().dp,
        ),
    ) {
        subToast.show(StringRes.locale.screenshotSuccess)

        ToasterContainer(
            toast = subToast,
        ) {
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.onClick(
                matcher = PointerMatcher.mouse(PointerButton.Secondary),
            ) {
                expanded = true
            }) {
                Image(
                    bitmap = encoded.toComposeImageBitmap(),
                    contentDescription = "preview",
                    contentScale = ContentScale.Crop,
                )

                CursorDropdownMenu(
                    onDismissRequest = {
                        expanded = false
                    },
                    expanded = expanded,
                ) {
                    DropdownMenuItem(onClick = {
                        expanded = false
                        subToast.show(StringRes.locale.screenshotRefresh)
                        viewModel.dispatch(ActivityAction.OnScreenshot(device))
                    }) {
                        Text(StringRes.locale.refresh)
                    }

                    DropdownMenuItem(onClick = {
                        expanded = false
                        val msgResult = MsgResult { msg: String, path: String? ->
                            subToast.show(msg)
                            if (!msg.contains("err") && !msg.contains("fail")) {
                                PathUtils.openDir(path)
                            }
                        }
                        viewModel.dispatch(ActivityAction.OnSaveScreenshot(device, msgResult))
                    }) {
                        Text(StringRes.locale.save)
                    }

                    DropdownMenuItem(onClick = {
                        expanded = false
                        onCloseRequest()
                    }) {
                        Text(StringRes.locale.close)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScrcpyConfigDialog() {
    val viewModel: ActivityViewModel = viewModel()
    val device = LocalDevice.current!!
    val show by viewModel.scrcpyDialog.collectAsState()

    if (!show) return

    var scrcpyPath by remember { mutableStateOf("") }
    CardContentDialog(
        onDismiss = { viewModel.dispatch(ActivityAction.OnScrcpyDialog(false)) },
        header = {
            Text(
                StringRes.locale.scrcpyTitle,
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(16.dp)
            )
        },
        actions = {
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = { viewModel.dispatch(ActivityAction.OnScrcpyDialog(false)) },
                modifier = Modifier.padding(bottom = 8.dp, end = 16.dp)
            ) {
                Text(StringRes.locale.cancel)
            }
            TextButton(
                onClick = {
                    if (scrcpyPath.isEmpty()) {
                        Toast.show(StringRes.locale.scrcpyPathEmpty)
                        return@TextButton
                    }
                    viewModel.dispatch(ActivityAction.OnSaveScrcpyPath(device, scrcpyPath))
                },
                modifier = Modifier.padding(bottom = 8.dp, end = 16.dp)
            ) {
                Text(StringRes.locale.save)
            }
        },
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp),
        ) {
            CardTextField(
                value = scrcpyPath,
                onValueChange = { scrcpyPath = it },
                placeholder = StringRes.locale.scrcpyPath,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}