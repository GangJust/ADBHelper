package common.compose

import androidx.compose.animation.*
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ExtendedMenuState(
    extended: Boolean,
    width: Int,
) {
    private val _isExtended = mutableStateOf(extended)

    val maxWidth = width

    val isExtended
        get() = _isExtended.value

    fun expand() {
        _isExtended.value = true
    }

    fun shrink() {
        _isExtended.value = false
    }

    fun toggle() {
        _isExtended.value = !_isExtended.value
    }
}

@Immutable
class ExtendedMenuScope(val state: ExtendedMenuState)

@Composable
fun rememberExtendedMenuState(
    extended: Boolean = true,
    width: Int = 220,
): ExtendedMenuState {
    return remember {
        ExtendedMenuState(extended, width)
    }
}

@Composable
fun ExtendedMenu(
    state: ExtendedMenuState = rememberExtendedMenuState(),
    elevation: Dp = 1.dp,
    header: @Composable ExtendedMenuScope.() -> Unit = {},
    footer: @Composable ExtendedMenuScope.() -> Unit = {},
    content: @Composable ExtendedMenuScope.() -> Unit,
) {
    val extendedMenu by remember { mutableStateOf(ExtendedMenuScope(state)) }

    Surface(
        elevation = elevation,
        modifier = Modifier
            .widthIn(max = state.maxWidth.dp)
            .fillMaxHeight(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            header(extendedMenu)
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                item {
                    content(extendedMenu)
                }
            }
            footer(extendedMenu)
        }
    }
}

@Composable
fun ExtendedMenuScope.ExtendedMenuItemBox(
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = LocalIndication.current,
    shape: Shape = RoundedCornerShape(16.dp),
    modifier: Modifier = Modifier,
    contentTransform: ContentTransform = (fadeIn()).togetherWith(fadeOut()),
    openContent: @Composable () -> Unit,
    shrinkContent: @Composable () -> Unit,
    boxContent: @Composable (content: @Composable () -> Unit) -> Unit,
) {
    AnimatedContent(
        targetState = state.isExtended,
        transitionSpec = { contentTransform },
        modifier = modifier
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = if (onClick != null) indication else null,
                onClick = { onClick?.invoke() },
            ),
    ) { extended ->
        if (extended) {
            boxContent(openContent)
        } else {
            boxContent(shrinkContent)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExtendedMenuScope.ExtendedMenuItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    selectedContentColor: Color = MaterialTheme.colors.primary,
    unselectedContentColor: Color = Color.Transparent,
) {
    Surface(
        color = if (selected) selectedContentColor else unselectedContentColor,
        contentColor = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = Modifier.padding(ExtendedMenuOutPadding),
    ) {
        Row(
            modifier = modifier.padding(ExtendedMenuInPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            AnimatedVisibility(
                visible = state.isExtended,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(),
            ) {
                Box(
                    modifier = Modifier
                        .padding(ExtendedMenuLabelPadding)
                        .fillMaxWidth(),
                ) {
                    label?.invoke()
                }
            }
        }
    }
}

private val ExtendedMenuOutPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

private val ExtendedMenuInPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)

private val ExtendedMenuLabelPadding = PaddingValues(start = 16.dp)

