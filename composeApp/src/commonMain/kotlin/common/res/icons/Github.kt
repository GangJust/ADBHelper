package common.res.icons

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

public val IconRes.Github: ImageVector
    get() {
        if (_github != null) {
            return _github!!
        }
        _github = Builder(name = "Github", defaultWidth = 200.0.dp, defaultHeight = 200.0.dp,
                viewportWidth = 1024.0f, viewportHeight = 1024.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(511.96f, 21.33f)
                curveTo(241.02f, 21.33f, 21.33f, 240.98f, 21.33f, 512.0f)
                curveToRelative(0.0f, 216.83f, 140.54f, 400.73f, 335.57f, 465.66f)
                curveToRelative(24.49f, 4.39f, 32.26f, -10.07f, 32.26f, -23.08f)
                curveToRelative(0.0f, -11.69f, 0.26f, -44.25f, 0.0f, -85.21f)
                curveToRelative(-136.45f, 29.61f, -164.74f, -64.64f, -164.74f, -64.64f)
                curveToRelative(-22.31f, -56.7f, -54.4f, -71.77f, -54.4f, -71.77f)
                curveToRelative(-44.59f, -30.46f, 3.29f, -29.82f, 3.29f, -29.82f)
                curveToRelative(49.19f, 3.41f, 75.18f, 50.52f, 75.18f, 50.52f)
                curveToRelative(43.78f, 75.01f, 114.82f, 53.33f, 142.76f, 40.79f)
                curveToRelative(4.52f, -31.66f, 17.15f, -53.38f, 31.19f, -65.54f)
                curveToRelative(-108.97f, -12.46f, -223.49f, -54.49f, -223.49f, -242.6f)
                curveToRelative(0.0f, -53.55f, 19.11f, -97.32f, 50.52f, -131.67f)
                curveToRelative(-5.03f, -12.33f, -21.93f, -62.29f, 4.78f, -129.83f)
                curveToRelative(0.0f, 0.0f, 41.26f, -13.18f, 134.91f, 50.35f)
                arcToRelative(469.8f, 469.8f, 0.0f, false, true, 122.88f, -16.55f)
                curveToRelative(41.64f, 0.21f, 83.63f, 5.63f, 122.88f, 16.55f)
                curveToRelative(93.65f, -63.49f, 134.78f, -50.35f, 134.78f, -50.35f)
                curveToRelative(26.75f, 67.54f, 9.9f, 117.5f, 4.86f, 129.83f)
                curveToRelative(31.4f, 34.35f, 50.47f, 78.12f, 50.47f, 131.67f)
                curveToRelative(0.0f, 188.59f, -114.73f, 230.02f, -224.04f, 242.09f)
                curveToRelative(17.58f, 15.23f, 33.58f, 44.67f, 33.58f, 90.45f)
                verticalLineToRelative(135.85f)
                curveToRelative(0.0f, 13.14f, 7.94f, 27.61f, 32.85f, 22.87f)
                curveTo(862.25f, 912.6f, 1002.67f, 728.75f, 1002.67f, 512.0f)
                curveTo(1002.67f, 240.98f, 783.02f, 21.33f, 511.96f, 21.33f)
                close()
            }
        }
        .build()
        return _github!!
    }

private var _github: ImageVector? = null
