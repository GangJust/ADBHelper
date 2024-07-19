package compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WindowContainer(
    cornerSize: Dp = 12.dp,
    content: @Composable () -> Unit,
) {
    Surface(
        elevation = 2.dp,
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(cornerSize),
        modifier = Modifier.padding(cornerSize / 2),
    ) {
        content()
    }
}