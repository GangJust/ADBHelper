package app.page

import LocalDevice
import adb.entity.FileDesc
import adbhelper.composeapp.generated.resources.Res
import adbhelper.composeapp.generated.resources.ic_apk_document
import adbhelper.composeapp.generated.resources.ic_arrow_forward
import adbhelper.composeapp.generated.resources.ic_bookmark
import adbhelper.composeapp.generated.resources.ic_bookmark_add
import adbhelper.composeapp.generated.resources.ic_close
import adbhelper.composeapp.generated.resources.ic_content_copy
import adbhelper.composeapp.generated.resources.ic_document
import adbhelper.composeapp.generated.resources.ic_file
import adbhelper.composeapp.generated.resources.ic_folder
import adbhelper.composeapp.generated.resources.ic_folder_zip
import adbhelper.composeapp.generated.resources.ic_home
import adbhelper.composeapp.generated.resources.ic_image_document
import adbhelper.composeapp.generated.resources.ic_link
import adbhelper.composeapp.generated.resources.ic_music_document
import adbhelper.composeapp.generated.resources.ic_refresh
import adbhelper.composeapp.generated.resources.ic_storage
import adbhelper.composeapp.generated.resources.ic_unknown_document
import adbhelper.composeapp.generated.resources.ic_video_document
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.viewmodel.FileListAction
import app.viewmodel.FileListViewModel
import common.compose.CardContentDialog
import common.compose.CardMessageDialog
import common.compose.SelectionText
import common.compose.Toast
import common.compose.WaitingDialog
import compose.ActionIconButton
import compose.DropContainer
import compose.common.utils.fileSize
import compose.common.utils.minus
import compose.common.view.CardTextField
import entity.Bookmark
import i18n.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mvi.MsgCallback
import org.jetbrains.compose.resources.painterResource
import window.LocalWindowScope
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.io.File

@Composable
fun FileListPage() {
    Scaffold(
        topBar = {
            TopBar()
        }
    ) {
        FileDropContainer {
            FileList()
        }
    }

    BookmarkDialog()

    BookmarkEditDialog()

    DetailDialog()

    DeleteDialog()

    LoadingDialog()
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun TopBar() {
    val viewModel: FileListViewModel = viewModel()
    val device = LocalDevice.current!!

    val currentPath by viewModel.currPath.collectAsState(Dispatchers.Main)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(vertical = 4.dp),
    ) {
        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(end = 4.dp),
            onClick = {
                viewModel.dispatch(FileListAction.GetFileList(device, "/"))
            },
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_home),
                contentDescription = "home",
                modifier = Modifier.padding(8.dp),
            )
        }
        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 4.dp),
            onClick = {
                viewModel.dispatch(FileListAction.GetFileList(device, "/storage/emulated/0/"))
            },
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_storage),
                contentDescription = "storage",
                modifier = Modifier.padding(8.dp),
            )
        }
        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 4.dp),
            onClick = {
                viewModel.dispatch(FileListAction.BookmarkDialog(true))
            }
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_bookmark),
                contentDescription = "bookmark",
                modifier = Modifier.padding(8.dp),
            )
        }
        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 4.dp).weight(1f),
        ) {
            CardTextField(
                value = currentPath,
                placeholder = StringRes.locale.pathPlaceholder,
                backgroundColor = Color.Transparent,
                singleLine = true,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                onValueChange = {
                    viewModel.dispatch(FileListAction.OnCurrPath(it))
                },
            )
        }
        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(start = 4.dp),
            onClick = {
                viewModel.dispatch(FileListAction.GetFileList(device, currentPath))
            },
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_forward),
                contentDescription = "arrow-forward",
                modifier = Modifier.padding(8.dp),
            )
        }
        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 4.dp),
            onClick = {
                viewModel.dispatch(FileListAction.GetFileList(device, currentPath))
            },
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_refresh),
                contentDescription = "refresh",
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
private fun FileDropContainer(
    content: @Composable () -> Unit,
) {
    val windowScope = LocalWindowScope.current
    val viewModel: FileListViewModel = viewModel()
    val device = LocalDevice.current!!

    var isDragging by remember { mutableStateOf(false) }

    DropContainer(
        window = windowScope.window,
        dropListener = {
            onDragEnter {
                isDragging = true
            }

            onDrop {
                isDragging = false
                acceptDrop(DnDConstants.ACTION_REFERENCE)
                val files = transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
                if (files.size > 1) {
                    Toast.show(StringRes.locale.multiFileMessage)
                } else {
                    val file = files[0] as File
                    val path = file.absolutePath
                    val msgCallback = MsgCallback { msg: String ->
                        Toast.show(msg)
                    }
                    viewModel.dispatch(FileListAction.OnPush(device, path, msgCallback))
                }
            }

            onDragExit {
                isDragging = false
            }
        },
    ) {
        content.invoke()

        if (isDragging) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.1f)),
            ) {
                Text(
                    text = StringRes.locale.filePushMessage,
                    style = MaterialTheme.typography.body2.copy(
                        color = LocalContentColor.current,
                    ),
                )
            }
        }
    }
}

// 文件列表|File List
@Composable
private fun FileList() {
    val viewModel: FileListViewModel = viewModel()
    val device = LocalDevice.current!!
    val scrollState = rememberLazyListState()

    val fileList by viewModel.fileList.collectAsState()

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            items(fileList) {
                FileListItem(
                    desc = it,
                    onPrimaryClick = { desc ->
                        if (desc.isFile || desc.isLinkFile) {
                            viewModel.dispatch(FileListAction.DetailDialog(desc))
                        } else {
                            viewModel.dispatch(FileListAction.GoDirectory(device, desc))
                        }
                    },
                    onSecondaryClick = { desc ->
                        if (desc.isSuperior) // 上级目录
                            return@FileListItem

                        viewModel.dispatch(FileListAction.DetailDialog(desc))
                    },
                )
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .align(Alignment.CenterEnd)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileListItem(
    desc: FileDesc,
    onPrimaryClick: (FileDesc) -> Unit,
    onSecondaryClick: ((FileDesc) -> Unit)? = null,
) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp,
            )
            .onClick(
                matcher = PointerMatcher.mouse(PointerButton.Primary)
            ) {
                onPrimaryClick.invoke(desc)
            }
            .onClick(
                matcher = PointerMatcher.mouse(PointerButton.Secondary)
            ) {
                onSecondaryClick?.invoke(desc)
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            FileListItemIcon(desc)

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = desc.name,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = desc.datetime,
                        style = MaterialTheme.typography.body2.copy(
                            color = LocalContentColor.current.copy(alpha = 0.5f),
                            fontSize = MaterialTheme.typography.body2.fontSize - 2.sp,
                        ),
                    )
                    if (desc.isFile) {
                        Text(
                            text = desc.size.fileSize(),
                            style = MaterialTheme.typography.body2.copy(
                                color = LocalContentColor.current.copy(alpha = 0.5f),
                                fontSize = MaterialTheme.typography.body2.fontSize - 2.sp,
                            ),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }

            if (desc.isLink) {
                Icon(
                    painter = painterResource(Res.drawable.ic_link),
                    contentDescription = desc.kind,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun FileListItemIcon(
    desc: FileDesc,
) {
    Icon(
        painter = painterResource(
            when (desc.kind) {
                "superior", "directory", "link-directory" -> {
                    Res.drawable.ic_folder
                }

                "file", "link-file" -> {
                    if (desc.isZip) {
                        Res.drawable.ic_folder_zip
                    } else if (desc.isDocument) {
                        Res.drawable.ic_document
                    } else if (desc.isApk) {
                        Res.drawable.ic_apk_document
                    } else if (desc.isImage) {
                        Res.drawable.ic_image_document
                    } else if (desc.isVideo) {
                        Res.drawable.ic_video_document
                    } else if (desc.isMusic) {
                        Res.drawable.ic_music_document
                    } else {
                        Res.drawable.ic_file
                    }
                }

                else -> {
                    Res.drawable.ic_file
                }
            }
        ),
        contentDescription = desc.kind,
        modifier = Modifier.size(32.dp)
    )
}

// 详细对话框|Detail Dialog
@Composable
private fun DetailDialog() {
    val viewModel: FileListViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val windowScope = LocalWindowScope.current
    val clipboardManager = LocalClipboardManager.current
    val device = LocalDevice.current!!

    val desc by viewModel.showDetail.collectAsState()
    desc ?: return // 无数据不显示

    val onDismiss = {
        viewModel.dispatch(FileListAction.DetailDialog(null))
    }

    CardContentDialog(
        onDismiss = onDismiss,
        modifier = Modifier.padding(horizontal = 24.dp),
        header = {
            windowScope.WindowDraggableArea {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = StringRes.locale.detail,
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ActionIconButton(
                        onClick = {
                            val msgCallback = MsgCallback { msg: String ->
                                Toast.show(msg)
                            }
                            viewModel.dispatch(
                                FileListAction.OnAddBookmark(
                                    device,
                                    Bookmark(desc!!.name, desc!!.absolutePath, desc!!.kind),
                                    msgCallback,
                                )
                            )
                        },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_bookmark_add),
                            contentDescription = "add bookmark",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    ActionIconButton(
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                clipboardManager.setText(AnnotatedString(desc!!.absolutePath))
                                withContext(Dispatchers.Main) {
                                    Toast.show(StringRes.locale.pathCopied)
                                }
                            }
                        },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_content_copy),
                            contentDescription = "copy",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    ActionIconButton(
                        onClick = onDismiss,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_close),
                            contentDescription = "close",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        },
        actions = {
            TextButton(
                onClick = {
                    onDismiss.invoke()
                    viewModel.dispatch(FileListAction.DeleteDialog(desc))
                },
                modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
            ) {
                Text(text = StringRes.locale.delete)
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    Toast.show(StringRes.locale.future)
                    // viewModel.dispatch(FileListAction.OnPermissions(device, desc))
                },
                modifier = Modifier.padding(end = 24.dp, bottom = 8.dp)
            ) {
                Text(text = StringRes.locale.permissions)
            }
            TextButton(
                onClick = {
                    val msgCallback = MsgCallback { msg: String ->
                        Toast.show(msg)
                        onDismiss.invoke()
                    }
                    viewModel.dispatch(FileListAction.OnPull(device, desc!!, msgCallback))
                },
                modifier = Modifier.padding(end = 24.dp, bottom = 8.dp)
            ) {
                Text(text = StringRes.locale.export)
            }
        }
    ) {
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        DetailItemAttribute(StringRes.locale.fileName, desc!!.name)
        DetailItemAttribute(StringRes.locale.fileDir, desc!!.path, false)
        DetailItemAttribute(StringRes.locale.fileKind, desc!!.kind)
        DetailItemAttribute(StringRes.locale.fileSize, desc!!.size.fileSize())
        DetailItemAttribute(StringRes.locale.filePermission, desc!!.permissionStr)
        DetailItemAttribute(StringRes.locale.fileOwner, desc!!.owner)
        DetailItemAttribute(StringRes.locale.fileGroup, desc!!.group)
        DetailItemAttribute(StringRes.locale.fileUpdate, desc!!.datetime)
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
    }
}

@Composable
private fun DetailItemAttribute(
    label: String,
    value: String,
    singleLine: Boolean = true,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp).fillMaxWidth(),
        content = {
            Text(
                modifier = Modifier.weight(3f),
                text = label,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
            SelectionText(
                value = value,
                placeholder = StringRes.locale.nothing,
                singleLine = singleLine,
                modifier = Modifier.padding(horizontal = 12.dp).weight(9f),
            )
        }
    )
}

// 删除对话框|Delete Dialog
@Composable
private fun DeleteDialog() {
    val viewModel: FileListViewModel = viewModel()
    val device = LocalDevice.current!!

    val desc by viewModel.showDelete.collectAsState()
    desc ?: return // 无数据不显示

    val onDismiss = {
        viewModel.dispatch(FileListAction.DeleteDialog(null))
    }

    CardMessageDialog(
        onDismiss = onDismiss,
        title = StringRes.locale.tipsTitle,
        message = StringRes.locale.fileDeleteMessage,
        cancel = StringRes.locale.cancel,
        confirm = StringRes.locale.confirm,
        onCancel = onDismiss,
        onConfirm = {
            val msgCallback = MsgCallback { msg: String ->
                Toast.show(msg)
                onDismiss.invoke()
            }
            viewModel.dispatch(FileListAction.OnDelete(device, desc!!, msgCallback))
        },
    )
}

// 书签对话框|Bookmark Dialog
@Composable
private fun BookmarkDialog() {
    val viewModel: FileListViewModel = viewModel()
    val windowScope = LocalWindowScope.current
    val device = LocalDevice.current!!

    val isShowing by viewModel.showBookmark.collectAsState()
    if (!isShowing)
        return

    val onDismiss = {
        viewModel.dispatch(FileListAction.BookmarkDialog(false))
    }

    LaunchedEffect(Unit) {
        viewModel.dispatch(FileListAction.GetBookmarks(device))
    }

    val bookmarkList by viewModel.bookmarks.collectAsState()

    CardContentDialog(
        onDismiss = { /* 不响应|unresponsive */ },
        modifier = Modifier.heightIn(max = 520.dp),
        header = {
            windowScope.WindowDraggableArea {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = StringRes.locale.bookmark,
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ActionIconButton(
                        onClick = onDismiss,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_close),
                            contentDescription = "close",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        },
        actions = { }
    ) {
        LazyColumn(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            if (bookmarkList.isEmpty()) {
                item {
                    Text(
                        text = StringRes.locale.bookmarkEmpty,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                    )
                }
            }

            items(bookmarkList) {
                BookmarkItem(
                    bookmark = it,
                    onClick = { bookmark ->
                        viewModel.dispatch(FileListAction.GoBookmark(device, bookmark))
                        onDismiss.invoke()
                    },
                    onEdit = { bookmark ->
                        viewModel.dispatch(FileListAction.BookmarkEditDialog(bookmark))
                    },
                    onDelete = { bookmark ->
                        val msgCallback = MsgCallback { msg: String ->
                            Toast.show(msg)
                        }
                        viewModel.dispatch(
                            FileListAction.OnDeleteBookmark(
                                device,
                                bookmark,
                                msgCallback
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun BookmarkItem(
    bookmark: Bookmark,
    onClick: (Bookmark) -> Unit,
    onEdit: (Bookmark) -> Unit,
    onDelete: (Bookmark) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onClick.invoke(bookmark)
            }
    ) {
        Spacer(Modifier.padding(horizontal = 8.dp))

        Icon(
            painter = if (bookmark.isDirectory) {
                painterResource(Res.drawable.ic_folder)
            } else if (bookmark.isFile) {
                painterResource(Res.drawable.ic_file)
            } else {
                painterResource(Res.drawable.ic_unknown_document)
            },
            contentDescription = bookmark.name,
            modifier = Modifier.size(32.dp),
        )

        Spacer(Modifier.padding(horizontal = 4.dp))

        Column {
            Text(
                text = bookmark.name,
                style = MaterialTheme.typography.body1,
            )
            Text(
                text = bookmark.path,
                style = MaterialTheme.typography.body2.copy(
                    color = LocalContentColor.current.copy(alpha = 0.5f),
                ),
            )
        }

        Spacer(Modifier.weight(1f))

        Box {
            IconButton(
                onClick = {
                    expanded = true
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "more",
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
            ) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onEdit.invoke(bookmark)
                    },
                ) {
                    Text(StringRes.locale.bookmarkEditItem)
                }
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onDelete.invoke(bookmark)
                    },
                ) {
                    Text(StringRes.locale.bookmarkDeleteItem)
                }
            }
        }
    }
}

// 书签编辑对话框|Bookmark Edit Dialog
@Composable
private fun BookmarkEditDialog() {
    val viewModel: FileListViewModel = viewModel()
    val device = LocalDevice.current!!
    val bookmark by viewModel.showBookmarkEdit.collectAsState()
    bookmark ?: return // 无数据不显示

    val onDismiss = {
        viewModel.dispatch(FileListAction.BookmarkEditDialog(null))
    }

    var name by remember { mutableStateOf(bookmark!!.name) }
    var path by remember { mutableStateOf(bookmark!!.path) }

    CardContentDialog(
        onDismiss = onDismiss,
        header = {
            Text(
                text = StringRes.locale.bookmarkEditTitle,
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(24.dp)
            )
        },
        actions = {
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(bottom = 8.dp, end = 16.dp)
            ) {
                Text(StringRes.locale.cancel)
            }
            TextButton(
                onClick = {
                    onDismiss.invoke()
                    val msgCallback = MsgCallback { msg: String ->
                        Toast.show(msg)
                    }
                    viewModel.dispatch(
                        FileListAction.OnSaveBookmark(
                            device,
                            bookmark!!.copy(name = name, path = path),
                            msgCallback,
                        )
                    )
                },
                modifier = Modifier.padding(bottom = 8.dp, end = 16.dp)
            ) {
                Text(StringRes.locale.save)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .padding(horizontal = 16.dp),
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = {
                    Text(StringRes.locale.bookmarkEditName)
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            TextField(
                value = path,
                onValueChange = { path = it },
                singleLine = true,
                label = {
                    Text(StringRes.locale.bookmarkEditPath)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// 加载对话框|Loading Dialog
@Composable
private fun LoadingDialog() {
    val viewModel: FileListViewModel = viewModel()
    val isWaiting by viewModel.isWaiting.collectAsState()

    WaitingDialog(
        isWaiting = isWaiting
    )
}