package dev.alnoer.audiosaas.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = BrandOrange,
    onPrimary = Paper,
    secondary = SecondaryLight,
    onSecondary = SecondaryLightForeground,
    background = CanvasLight,
    onBackground = Ink950,
    surface = SurfaceLight,
    onSurface = Ink950,
    surfaceVariant = MutedLight,
    onSurfaceVariant = LightMutedForeground,
    error = ErrorLight,
    onError = SurfaceLight,
    outline = LightBorder,
    outlineVariant = LightBorder,
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandOrange,
    onPrimary = Paper,
    secondary = Zinc800,
    onSecondary = Color(0xFFFAFAFA),
    background = Ink950,
    onBackground = Paper,
    surface = Ink900,
    onSurface = Paper,
    surfaceVariant = Ink800,
    onSurfaceVariant = MutedForeground,
    error = ErrorDark,
    onError = SurfaceLight,
    outline = Color.White.copy(alpha = 0.10f),
    outlineVariant = Color.White.copy(alpha = 0.10f),
)

object AudioSpacing {
    val none = 0.dp
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

val MaterialTheme.audioColors: AudioColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAudioColors.current

@Composable
fun AudioSaaSTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkAudioColors else LightAudioColors

    CompositionLocalProvider(LocalAudioColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AudioTypography,
            content = content,
        )
    }
}
