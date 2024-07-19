package common.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

@Composable
fun AppTheme(
    isDark: Boolean = currentSystemTheme == SystemTheme.DARK,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (!isDark) appLightColor() else appDarkColor(),
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content,
    )
}

@Composable
fun appLightColor(): Colors {
    return lightColors(
        primary = Color(0xFF216C2E),
        primaryVariant = Color(0xFF13401B),
        secondary = Color(0xFF53634E),
        secondaryVariant = Color(0xFFD2E1D5),
        background = Color(0xFFFCFDF7),
        surface = Color(0xFFFCFDF7),
        error = Color(0xFFBA1A1A),
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFFFFFFFF),
        onBackground = Color(0xFF1A1C19),
        onSurface = Color(0xFF1A1C19),
        onError = Color(0xFFFFFFFF),
    )
}

fun appDarkColor(): Colors {
    return darkColors(
        primary = Color(0xFF6FDBA8),
        primaryVariant = Color(0xFF3D795C),
        secondary = Color(0xFFB3CCBE),
        secondaryVariant = Color(0xFF354B40),
        background = Color(0xFF1D2521),
        surface = Color(0xFF1D2521),
        error = Color(0xFFFFB4AB),
        onPrimary = Color(0xFF003824),
        onSecondary = Color(0xFF1F352A),
        onBackground = Color(0xFFE1E3DF),
        onSurface = Color(0xFFE1E3DF),
        onError = Color(0xFF690005),
    )
}