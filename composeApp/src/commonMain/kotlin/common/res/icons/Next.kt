package compose.common.res.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import common.res.IconRes

public val IconRes.Next: ImageVector
    get() {
        if (_next != null) {
            return _next!!
        }
        _next = Builder(
            name = "Next", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 960.0f, viewportHeight = 960.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFe8eaed)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(646.0f, 520.0f)
                lineTo(200.0f, 520.0f)
                quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
                reflectiveQuadTo(160.0f, 480.0f)
                quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
                reflectiveQuadTo(200.0f, 440.0f)
                horizontalLineToRelative(446.0f)
                lineTo(532.0f, 326.0f)
                quadToRelative(-12.0f, -12.0f, -11.5f, -28.0f)
                reflectiveQuadToRelative(11.5f, -28.0f)
                quadToRelative(12.0f, -12.0f, 28.5f, -12.5f)
                reflectiveQuadTo(589.0f, 269.0f)
                lineToRelative(183.0f, 183.0f)
                quadToRelative(6.0f, 6.0f, 8.5f, 13.0f)
                reflectiveQuadToRelative(2.5f, 15.0f)
                quadToRelative(0.0f, 8.0f, -2.5f, 15.0f)
                reflectiveQuadToRelative(-8.5f, 13.0f)
                lineTo(589.0f, 691.0f)
                quadToRelative(-12.0f, 12.0f, -28.5f, 11.5f)
                reflectiveQuadTo(532.0f, 690.0f)
                quadToRelative(-11.0f, -12.0f, -11.5f, -28.0f)
                reflectiveQuadToRelative(11.5f, -28.0f)
                lineToRelative(114.0f, -114.0f)
                close()
            }
        }
            .build()
        return _next!!
    }

private var _next: ImageVector? = null
