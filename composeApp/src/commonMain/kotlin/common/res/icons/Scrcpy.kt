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

public val IconRes.Scrcpy: ImageVector
    get() {
        if (_scrcpy != null) {
            return _scrcpy!!
        }
        _scrcpy = Builder(
            name = "Scrcpy", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 960.0f, viewportHeight = 960.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF5f6368)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(440.0f, 440.0f)
                horizontalLineToRelative(80.0f)
                verticalLineToRelative(56.0f)
                quadToRelative(0.0f, 7.0f, 6.0f, 9.5f)
                reflectiveQuadToRelative(11.0f, -2.5f)
                lineToRelative(89.0f, -89.0f)
                quadToRelative(6.0f, -6.0f, 6.0f, -14.0f)
                reflectiveQuadToRelative(-6.0f, -14.0f)
                lineToRelative(-89.0f, -89.0f)
                quadToRelative(-5.0f, -5.0f, -11.0f, -2.5f)
                reflectiveQuadToRelative(-6.0f, 9.5f)
                verticalLineToRelative(56.0f)
                horizontalLineToRelative(-80.0f)
                quadToRelative(-50.0f, 0.0f, -85.0f, 35.0f)
                reflectiveQuadToRelative(-35.0f, 85.0f)
                verticalLineToRelative(40.0f)
                quadToRelative(0.0f, 17.0f, 11.5f, 28.5f)
                reflectiveQuadTo(360.0f, 560.0f)
                quadToRelative(17.0f, 0.0f, 28.5f, -11.5f)
                reflectiveQuadTo(400.0f, 520.0f)
                verticalLineToRelative(-40.0f)
                quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
                reflectiveQuadTo(440.0f, 440.0f)
                close()
                moveTo(80.0f, 840.0f)
                quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
                reflectiveQuadTo(40.0f, 800.0f)
                quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
                reflectiveQuadTo(80.0f, 760.0f)
                horizontalLineToRelative(800.0f)
                quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
                reflectiveQuadTo(920.0f, 800.0f)
                quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
                reflectiveQuadTo(880.0f, 840.0f)
                lineTo(80.0f, 840.0f)
                close()
                moveTo(160.0f, 720.0f)
                quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
                reflectiveQuadTo(80.0f, 640.0f)
                verticalLineToRelative(-440.0f)
                quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
                reflectiveQuadTo(160.0f, 120.0f)
                horizontalLineToRelative(640.0f)
                quadToRelative(33.0f, 0.0f, 56.5f, 23.5f)
                reflectiveQuadTo(880.0f, 200.0f)
                verticalLineToRelative(440.0f)
                quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
                reflectiveQuadTo(800.0f, 720.0f)
                lineTo(160.0f, 720.0f)
                close()
            }
        }
            .build()
        return _scrcpy!!
    }

private var _scrcpy: ImageVector? = null
