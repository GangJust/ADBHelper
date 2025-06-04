package io.github.adbhelper.common.res

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.adbhelper.common.res.icons.Github
import io.github.adbhelper.common.res.icons.MoreVert
import io.github.adbhelper.common.res.icons.Next
import io.github.adbhelper.common.res.icons.Scrcpy
import kotlin.collections.List as _KtList

private var all: _KtList<ImageVector>? = null

object IconRes

val IconRes.All: _KtList<ImageVector>
    get() {
        return all ?: listOf(
            Github,
            Next,
            Scrcpy,
            MoreVert,
        ).also { all = it }
    }
