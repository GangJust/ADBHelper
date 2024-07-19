package compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInWindow
import java.awt.Rectangle
import java.awt.dnd.*
import javax.swing.JPanel
import kotlin.math.roundToInt

typealias OnDragOverBlock = DropTargetDragEvent.() -> Unit
typealias OnDragEnter = DropTargetDragEvent.() -> Unit
typealias OnDropActionChanged = DropTargetDragEvent.() -> Unit
typealias OnDragExit = DropTargetEvent.() -> Unit
typealias OnDrop = DropTargetDropEvent.() -> Unit

interface DropListener {
    fun onDragOver(block: OnDragOverBlock)
    fun onDragEnter(block: OnDragEnter)
    fun onDropActionChanged(block: OnDropActionChanged)
    fun onDragExit(block: OnDragExit)
    fun onDrop(block: OnDrop)
}

class DropListenerImpl : DropListener {
    var onDragOverBlock: OnDragOverBlock? = null
    var onDragEnter: OnDragEnter? = null
    var onDropActionChanged: OnDropActionChanged? = null
    var onDragExit: OnDragExit? = null
    var onDrop: OnDrop? = null

    override fun onDragOver(block: OnDragOverBlock) {
        onDragOverBlock = block
    }

    override fun onDragEnter(block: OnDragEnter) {
        onDragEnter = block
    }

    override fun onDropActionChanged(block: OnDropActionChanged) {
        onDropActionChanged = block
    }

    override fun onDragExit(block: OnDragExit) {
        onDragExit = block
    }

    override fun onDrop(block: OnDrop) {
        onDrop = block
    }
}

// see at: https://juejin.cn/post/7233951543115776055?searchId=20240116171741D2970B8A64FA5F9845ED#heading-7
@Composable
fun DropContainer(
    window: ComposeWindow,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    component: JPanel = JPanel(),
    dropListener: DropListener.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val listener by remember { mutableStateOf(DropListenerImpl()) }
    val rectangle by remember { mutableStateOf(Rectangle()) }

    Box(
        contentAlignment = contentAlignment,
        modifier = modifier.onPlaced {
            rectangle.apply {
                x = it.positionInWindow().x.roundToInt()
                y = it.positionInWindow().y.roundToInt()
                width = it.size.width
                height = it.size.height
            }
        }
    ) {
        LaunchedEffect(Unit) {
            component.bounds = rectangle

            window.contentPane.add(component)

            dropListener.invoke(listener)
            component.dropTarget = object : DropTarget(
                component,
                object : DropTargetAdapter() {
                    override fun dragOver(dtde: DropTargetDragEvent) {
                        listener.onDragOverBlock?.invoke(dtde)
                    }

                    override fun dragEnter(dtde: DropTargetDragEvent) {
                        listener.onDragEnter?.invoke(dtde)
                    }

                    override fun dropActionChanged(dtde: DropTargetDragEvent) {
                        listener.onDropActionChanged?.invoke(dtde)
                    }

                    override fun dragExit(dte: DropTargetEvent) {
                        listener.onDragExit?.invoke(dte)
                    }

                    override fun drop(dtde: DropTargetDropEvent) {
                        listener.onDrop?.invoke(dtde)
                    }
                },
            ) {}
        }

        SideEffect {
            component.bounds = rectangle
        }

        DisposableEffect(Unit) {
            onDispose {
                window.contentPane.remove(component)
            }
        }

        content()
    }
}