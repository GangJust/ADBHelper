package compose.common.utils

import androidx.compose.ui.unit.TextUnit


internal operator fun TextUnit.minus(other: TextUnit): TextUnit {
    return TextUnit(value - other.value, type)
}

internal operator fun TextUnit.plus(other: TextUnit): TextUnit {
    return TextUnit(value + other.value, type)
}

fun String.fileSize(): String {
    val iSize = this.toIntOrNull() ?: 0

    val kb = iSize / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    val tb = gb / 1024.0

    return if (tb >= 1) {
        "${String.format("%.2f", tb)}T"
    } else if (gb >= 1) {
        "${String.format("%.2f", gb)}G"
    } else if (mb >= 1) {
        "${String.format("%.2f", mb)}M"
    } else if (kb >= 1) {
        "${String.format("%.2f", kb)}K"
    } else {
        "${iSize}B"
    }
}

