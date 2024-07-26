import adb.AdbServer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import common.compose.CardButton
import common.compose.Toast
import common.compose.ToasterContainer
import common.res.IconRes
import common.theme.AppTheme
import compose.WindowContainer
import compose.WindowTopBar
import compose.common.res.icons.Next
import compose.common.view.CardTextField
import entity.AppConfig
import i18n.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import window.AppWindow
import java.io.File

fun main() = application {
    val config = AppConfig.read()
    val adbPath = config.adbPath
    if (adbPath.isEmpty() || !File(adbPath).exists()) {
        InitApp()
    } else {
        MainApp()
    }
}

// Common Wrapper
//   - Window Style
//   - App Theme
//   - Toast
@Composable
fun AppContentWrapper(
    content: @Composable () -> Unit,
) {
    WindowContainer {
        AppTheme {
            ToasterContainer {
                content()
            }
        }
    }
}

@Composable
fun ApplicationScope.InitApp() {
    AppWindow(
        onCloseRequest = ::exitApplication,
        title = StringRes.locale.title,
        state = rememberWindowState(
            width = 520.dp,
            height = 280.dp,
            position = WindowPosition(Alignment.Center),
        ),
        transparent = true,
        undecorated = true,
        resizable = false,
    ) {
        AppContentWrapper {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    this.WindowDraggableArea {
                        WindowTopBar(
                            onCloseRequest = ::exitApplication,
                            title = {
                                Text(
                                    text = window.title,
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                                )
                            }
                        )
                    }
                }
            ) {
                val scope = rememberCoroutineScope()
                var pathValue by remember { mutableStateOf("") }
                var isWaiting by remember { mutableStateOf(false) }
                var susscess by remember { mutableStateOf(false) }
                val onNextClick = {
                    if (pathValue.isNotBlank()) {
                        scope.launch {
                            runCatching {
                                isWaiting = true
                                val result = withContext(Dispatchers.IO) { AdbServer.test(pathValue) }
                                isWaiting = false
                                susscess = result.isBlank() || result.contains("successfully")
                                if (!susscess) {
                                    Toast.show(result.trim())
                                }
                            }.onFailure { e ->
                                Toast.show(e.message ?: "unknown error.")
                            }
                        }
                    }
                }

                if (susscess) {
                    LaunchedEffect(Unit) {
                        // 保存配置
                        AppConfig.write(adbPath = pathValue)

                        // 退出应用
                        delay(3000)
                        exitApplication()
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = StringRes.locale.initSuccess,
                            style = MaterialTheme.typography.body1,
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                    ) {
                        Text(
                            text = StringRes.locale.initDescription,
                            style = MaterialTheme.typography.body1.copy(
                                color = LocalContentColor.current.copy(alpha = 0.8f),
                            ),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.padding(vertical = 8.dp))

                        CardTextField(
                            value = pathValue,
                            placeholder = StringRes.locale.initPlaceholder,
                            singleLine = true,
                            onValueChange = { pathValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        )

                        Spacer(modifier = Modifier.padding(vertical = 12.dp))

                        CardButton(
                            onClick = onNextClick,
                            shape = CircleShape,
                            enabled = !isWaiting,
                            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 12.dp),
                        ) {
                            if (isWaiting) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp),
                                )
                            } else {
                                Icon(
                                    imageVector = IconRes.Next,
                                    contentDescription = StringRes.locale.initNext,
                                    modifier = Modifier.size(24.dp),
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
fun ApplicationScope.MainApp() {
    AppWindow(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            width = 1080.dp,
            height = 720.dp,
            position = WindowPosition(Alignment.Center),
        ),
        title = StringRes.locale.title,
        resizable = false,
        undecorated = true,
        transparent = true,
    ) {
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutineScope.launch(Dispatchers.IO) {
                val config = AppConfig.read()
                AdbServer.initialize(config.adbPath)
                AdbServer.instance.startServer()
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                AdbServer.instance.killServer()
            }
        }

        AdbHelperApp(::exitApplication)
    }
}