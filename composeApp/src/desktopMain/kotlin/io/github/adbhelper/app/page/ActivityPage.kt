package io.github.adbhelper.app.page

import adbhelper.composeapp.generated.resources.Res
import adbhelper.composeapp.generated.resources.ic_handoff
import adbhelper.composeapp.generated.resources.ic_refresh
import adbhelper.composeapp.generated.resources.ic_screenshot
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import compose.common.view.CardTextField
import io.github.adbhelper.LocalDevice
import io.github.adbhelper.app.viewmodel.ActivityAction
import io.github.adbhelper.app.viewmodel.ActivityViewModel
import io.github.adbhelper.common.compose.CardContentDialog
import io.github.adbhelper.common.compose.SelectionText
import io.github.adbhelper.common.compose.Toast
import io.github.adbhelper.common.compose.ToasterContainer
import io.github.adbhelper.common.compose.rememberToast
import io.github.adbhelper.common.res.IconRes
import io.github.adbhelper.common.res.icons.Scrcpy
import io.github.adbhelper.composeViewModel
import io.github.adbhelper.i18n.StringRes
import io.github.adbhelper.mvi.MsgCallback
import io.github.adbhelper.mvi.MsgResult
import io.github.adbhelper.utils.PathUtils
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.Image

@Composable
fun ActivityPage(
    model: ActivityViewModel = composeViewModel(),
) {
    val device = LocalDevice.current
    LaunchedEffect(device) {
        model.dispatch(ActivityAction.Refresh(device))
    }

    Scaffold(
        floatingActionButton = {
            FloatButtons(model = model)
        },
    ) {
        val activity by model.activity.collectAsState()
        val handoff by model.toggleFullClassName.collectAsState()

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

    ScreenshotDialogWindow(model = model)

    ScrcpyConfigDialog(model = model)
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
private fun FloatButtons(
    model: ActivityViewModel,
) {
    val device = LocalDevice.current

    Column {
        FloatingActionButton(
            onClick = {
                val callback = MsgCallback {
                    Toast.show(it)
                }
                model.dispatch(ActivityAction.StartScrcpy(device, callback))
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
                model.dispatch(ActivityAction.Screenshot(device))
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
                model.dispatch(ActivityAction.ToggleFullClassName)
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
                model.dispatch(ActivityAction.Refresh(device))
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
private fun ScreenshotDialogWindow(
    model: ActivityViewModel,
) {
    val device = LocalDevice.current
    val screenshot by model.screenshot.collectAsState()

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
        model.dispatch(ActivityAction.ClearScreenshot)
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
            Box(
                modifier = Modifier.onClick(
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
                        model.dispatch(ActivityAction.Screenshot(device))
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
                        model.dispatch(ActivityAction.SaveScreenshot(device, msgResult))
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
private fun ScrcpyConfigDialog(
    model: ActivityViewModel,
) {
    val device = LocalDevice.current
    val show by model.scrcpyDialog.collectAsState()

    if (!show) return

    var scrcpyPath by remember { mutableStateOf("") }
    CardContentDialog(
        onDismiss = { model.dispatch(ActivityAction.ScrcpyDialog(false)) },
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
                onClick = { model.dispatch(ActivityAction.ScrcpyDialog(false)) },
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
                    model.dispatch(ActivityAction.SaveScrcpyPath(device, scrcpyPath))
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