package common.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TabItem(
    modifier: Modifier = Modifier,
    index: Int,
    title: String,
    onSelect: (Int) -> Unit,
    selected: Boolean = false,
    selectedContentColor: Color = MaterialTheme.colors.primary,
    unselectedContentColor: Color = Color.Transparent,
) {
    Surface(
        modifier = modifier.padding(horizontal = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) selectedContentColor else unselectedContentColor,
        contentColor = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
        onClick = {
            onSelect.invoke(index)
        },
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}