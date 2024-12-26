package compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInWindow
import java.awt.Rectangle
import java.awt.dnd.*
import javax.swing.JPanel
import kotlin.math.roundToInt

interface DropListener {
    fun onDragOver(block: DropTargetDragEvent.() -> Unit)
    fun onDragEnter(block: DropTargetDragEvent.() -> Unit)
    fun onDropActionChanged(block: DropTargetDragEvent.() -> Unit)
    fun onDragExit(block: DropTargetEvent.() -> Unit)
    fun onDrop(block: DropTargetDropEvent.() -> Unit)
}

class DropListenerImpl : DropListener {
    var onDragOverBlock: (DropTargetDragEvent.() -> Unit)? = null
    var onDragEnter: (DropTargetDragEvent.() -> Unit)? = null
    var onDropActionChanged: (DropTargetDragEvent.() -> Unit)? = null
    var onDragExit: (DropTargetEvent.() -> Unit)? = null
    var onDrop: (DropTargetDropEvent.() -> Unit)? = null

    override fun onDragOver(block: DropTargetDragEvent.() -> Unit) {
        onDragOverBlock = block
    }

    override fun onDragEnter(block: DropTargetDragEvent.() -> Unit) {
        onDragEnter = block
    }

    override fun onDropActionChanged(block: DropTargetDragEvent.() -> Unit) {
        onDropActionChanged = block
    }

    override fun onDragExit(block: DropTargetEvent.() -> Unit) {
        onDragExit = block
    }

    override fun onDrop(block: DropTargetDropEvent.() -> Unit) {
        onDrop = block
    }
}

// see at: https://juejin.cn/post/7233951543115776055?searchId=20240116171741D2970B8A64FA5F9845ED#heading-7
@Deprecated("use DropTargetContainer")
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


interface DropTargetListener {
    fun onStarted(block: DragAndDropEvent.() -> Unit)
    fun onChanged(block: DragAndDropEvent.() -> Unit)
    fun onEnded(block: DragAndDropEvent.() -> Unit)
    fun onEntered(block: DragAndDropEvent.() -> Unit)
    fun onExited(block: DragAndDropEvent.() -> Unit)
    fun onMoved(block: DragAndDropEvent.() -> Unit)
    fun onDrop(block: DragAndDropEvent.() -> Boolean)
}

class DropTargetListenerImpl : DropTargetListener {
    var onStarted: (DragAndDropEvent.() -> Unit)? = null
    var onChanged: (DragAndDropEvent.() -> Unit)? = null
    var onEnded: (DragAndDropEvent.() -> Unit)? = null
    var onEntered: (DragAndDropEvent.() -> Unit)? = null
    var onExited: (DragAndDropEvent.() -> Unit)? = null
    var onMoved: (DragAndDropEvent.() -> Unit)? = null
    var onDrop: (DragAndDropEvent.() -> Boolean)? = null

    override fun onStarted(block: DragAndDropEvent.() -> Unit) {
        this.onStarted = block
    }

    override fun onChanged(block: DragAndDropEvent.() -> Unit) {
        this.onChanged = block
    }

    override fun onEnded(block: DragAndDropEvent.() -> Unit) {
        this.onEnded = block
    }

    override fun onEntered(block: DragAndDropEvent.() -> Unit) {
        this.onEntered = block
    }

    override fun onExited(block: DragAndDropEvent.() -> Unit) {
        this.onExited = block
    }

    override fun onMoved(block: DragAndDropEvent.() -> Unit) {
        this.onMoved = block
    }

    override fun onDrop(block: DragAndDropEvent.() -> Boolean) {
        this.onDrop = block
    }
}

// see at: https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-drag-drop.html#creating-a-drop-target
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragAndDropContainer(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    shouldStartDragAndDrop: (startEvent: DragAndDropEvent) -> Boolean = { true },
    dropListener: DropTargetListener.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val listener by remember { mutableStateOf(DropTargetListenerImpl().apply(dropListener)) }

    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onStarted(event: DragAndDropEvent) {
                listener.onStarted?.invoke(event)
            }

            override fun onChanged(event: DragAndDropEvent) {
                listener.onChanged?.invoke(event)
            }

            override fun onEnded(event: DragAndDropEvent) {
                listener.onEnded?.invoke(event)
            }

            override fun onEntered(event: DragAndDropEvent) {
                listener.onEntered?.invoke(event)
            }

            override fun onExited(event: DragAndDropEvent) {
                listener.onExited?.invoke(event)
            }

            override fun onMoved(event: DragAndDropEvent) {
                listener.onMoved?.invoke(event)
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                return listener.onDrop?.invoke(event) == true
            }
        }
    }

    Box(
        contentAlignment = contentAlignment,
        modifier = modifier
            .dragAndDropTarget(
                // With "true" as the value of shouldStartDragAndDrop,
                // drag-and-drop operations are enabled unconditionally.
                shouldStartDragAndDrop = shouldStartDragAndDrop,
                target = dragAndDropTarget
            ),
        content = content,
    )
}