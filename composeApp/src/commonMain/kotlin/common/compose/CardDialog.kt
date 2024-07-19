package common.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CardMessageDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier.width(480.dp),
    isLoading: Boolean = false,
    title: String,
    message: String,
    cancel: String,
    confirm: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    CardMessageDialog(
        onDismiss = onDismiss,
        modifier = modifier,
        isLoading = isLoading,
        title = title,
        message = AnnotatedString(message),
        cancel = cancel,
        confirm = confirm,
        onCancel = onCancel,
        onConfirm = onConfirm,
    )
}

@Composable
fun CardMessageDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier.width(480.dp),
    isLoading: Boolean = false,
    title: String,
    message: AnnotatedString,
    cancel: String,
    confirm: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    CardContentDialog(
        onDismiss = onDismiss,
        modifier = modifier,
        header = {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
        },
        actions = {
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = onCancel,
                enabled = !isLoading,
                modifier = Modifier.padding(end = 16.dp, bottom = 8.dp)
            ) {
                Text(text = cancel)
            }
            TextButton(
                onClick = onConfirm,
                enabled = !isLoading,
                modifier = Modifier.padding(end = 16.dp, bottom = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    Text(text = confirm)
                }
            }
        }
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
        )
    }
}

@Composable
fun CardContentDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    CardDialog(
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                header()
                content()
                Row {
                    actions()
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WaitingDialog(
    isWaiting: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isWaiting) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                scrimColor = Color.Gray.copy(alpha = 0.5f),
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier,
            ) {
                CircularProgressIndicator(
                    strokeWidth = 4.dp,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
fun CardDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier,
        ) {
            content()
        }
    }
}