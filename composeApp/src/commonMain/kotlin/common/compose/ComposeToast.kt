package common.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Immutable
data class ToastEntity(
    val message: String,
    val duration: Duration,
)

@Immutable
class ToastState {
    private val _queue = MutableStateFlow(listOf<ToastEntity>())

    val queue: StateFlow<List<ToastEntity>>
        get() = _queue

    fun add(entity: ToastEntity) {
        _queue.value += entity
    }

    fun remove(entity: ToastEntity) {
        _queue.value -= entity
    }
}

class Toast {
    val state = ToastState()

    fun show(
        message: String,
        duration: Duration = 3.toDuration(DurationUnit.SECONDS),
    ) {
        state.add(
            ToastEntity(
                message = message,
                duration = duration,
            )
        )
    }

    companion object {
        val defaultStorage = ToastState()

        fun show(
            message: String,
            duration: Duration = 3.toDuration(DurationUnit.SECONDS),
        ) {
            defaultStorage.add(
                ToastEntity(
                    message = message,
                    duration = duration,
                )
            )
        }
    }
}

@Composable
private fun ToastView(
    state: ToastState,
) {
    Popup(
        alignment = Alignment.BottomCenter
    ) {
        val queue by state.queue.collectAsState()
        var toggle by remember { mutableStateOf(false) }

        val entity = queue.firstOrNull()
        if (entity != null) {
            AnimatedVisibility(
                visible = toggle,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                Card(
                    elevation = 4.dp,
                    backgroundColor = MaterialTheme.colors.secondary,
                    shape = RoundedCornerShape(48.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = entity.message,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }

        LaunchedEffect(queue) {
            queue.forEach {
                toggle = true
                delay(it.duration)
                toggle = false
                delay(300L) // 等待动画结束, 移除被展示过的toast, 进入下一次循环|Waiting for the animation to end
                state.remove(it)
            }
        }
    }
}

@Composable
fun ToasterContainer(
    toast: Toast? = null,
    content: @Composable () -> Unit,
) {
    content()

    ToastView(toast?.state ?: Toast.defaultStorage)
}

@Composable
fun rememberToast(): Toast {
    return remember { Toast() }
}