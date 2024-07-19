package compose.common.view

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun CardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    enabled: Boolean = true,
    backgroundColor: Color = TextFieldDefaults.textFieldColors().backgroundColor(enabled).value,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    singleLine: Boolean = false,
) {

    Card(
        backgroundColor = backgroundColor,
        shape = shape,
        elevation = 0.dp,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle.copy(color = LocalContentColor.current),
            singleLine = singleLine,
            cursorBrush = SolidColor(LocalContentColor.current.copy(alpha = LocalContentAlpha.current)),
            decorationBox = { innerTextField ->
                innerTextField.invoke()
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle.copy(
                            color = LocalContentColor.current.copy(alpha = 0.5f),
                            textAlign = MaterialTheme.typography.body2.textAlign,
                        ),
                    )
                }
            }
        )
    }
}