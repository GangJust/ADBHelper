package common.res

import androidx.compose.ui.graphics.vector.ImageVector
import common.res.icons.Github
import kotlin.collections.List as _KtList

private var all: _KtList<ImageVector>? = null

object IconRes

val IconRes.All: _KtList<ImageVector>
    get() {
        return all ?: listOf(
            Github,
        ).also {
            all = it
        }
    }
