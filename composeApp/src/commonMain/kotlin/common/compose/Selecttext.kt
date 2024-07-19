package common.compose

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SelectionText(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String = "",
    textAlign: TextAlign? = null,
    singleLine: Boolean = false,
) { // other see: SelectionContainer
    BasicTextField(
        modifier = modifier,
        value = value,
        cursorBrush = SolidColor(LocalContentColor.current.copy(alpha = LocalContentAlpha.current)),
        singleLine = singleLine,
        enabled = true,
        readOnly = true,
        textStyle = MaterialTheme.typography.body1.copy(
            color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
            textAlign = textAlign ?: MaterialTheme.typography.body1.textAlign,
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Proportional,
                trim = LineHeightStyle.Trim.Both,
            )
        ),
        onValueChange = { /* prohibit editing */ },
        decorationBox = { innerTextField ->
            innerTextField.invoke()
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.body2.copy(
                        color = LocalContentColor.current.copy(alpha = 0.5f),
                        textAlign = textAlign ?: MaterialTheme.typography.body2.textAlign,
                    ),
                )
            }
        },
    )
}