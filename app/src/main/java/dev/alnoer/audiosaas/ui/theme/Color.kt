package dev.alnoer.audiosaas.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val Ink950 = Color(0xFF0C0A09)
internal val Ink900 = Color(0xFF1C1917)
internal val Ink800 = Color(0xFF292524)
internal val Zinc800 = Color(0xFF27272A)
internal val Paper = Color(0xFFFFFBEB)
internal val CanvasLight = Color(0xFFF5F5F3)
internal val SurfaceLight = Color(0xFFFFFFFF)
internal val MutedLight = Color(0xFFF5F5F4)
internal val SecondaryLight = Color(0xFFF4F4F5)
internal val SecondaryLightForeground = Color(0xFF18181B)
internal val MutedForeground = Color(0xFFA6A09B)
internal val LightMutedForeground = Color(0xFF79716B)
internal val LightBorder = Color(0xFFE7E5E4)
internal val BrandOrange = Color(0xFFDB762F)
internal val ErrorLight = Color(0xFFE7000B)
internal val ErrorDark = Color(0xFFFF6467)

@Immutable
data class AudioColors(
    val card: Color,
    val cardForeground: Color,
    val popover: Color,
    val popoverForeground: Color,
    val muted: Color,
    val mutedForeground: Color,
    val accent: Color,
    val accentForeground: Color,
    val border: Color,
    val input: Color,
    val ring: Color,
    val destructiveForeground: Color,
    val sidebar: Color,
    val sidebarForeground: Color,
    val sidebarAccent: Color,
    val sidebarAccentForeground: Color,
    val sidebarBorder: Color,
    val chart1: Color,
    val chart2: Color,
    val chart3: Color,
    val chart4: Color,
    val chart5: Color,
    val onboardingAccent: Color,
    val onboardingBorder: Color,
    val onboardingMuted: Color,
)

internal val LightAudioColors = AudioColors(
    card = SurfaceLight,
    cardForeground = Ink950,
    popover = SurfaceLight,
    popoverForeground = Ink950,
    muted = MutedLight,
    mutedForeground = LightMutedForeground,
    accent = MutedLight,
    accentForeground = Ink900,
    border = LightBorder,
    input = LightBorder,
    ring = MutedForeground,
    destructiveForeground = SurfaceLight,
    sidebar = Color(0xFFFAFAF9),
    sidebarForeground = Ink950,
    sidebarAccent = MutedLight,
    sidebarAccentForeground = Ink900,
    sidebarBorder = LightBorder,
    chart1 = Color(0xFFD6D3D1),
    chart2 = Color(0xFF79716B),
    chart3 = Color(0xFF57534D),
    chart4 = Color(0xFF443F3B),
    chart5 = Color(0xFF292524),
    onboardingAccent = Color(0xFFE5611A),
    onboardingBorder = Color(0xFF3D3833),
    onboardingMuted = Color(0xFF8C8780),
)

internal val DarkAudioColors = AudioColors(
    card = Ink900,
    cardForeground = Paper,
    popover = Ink900,
    popoverForeground = Paper,
    muted = Ink800,
    mutedForeground = MutedForeground,
    accent = Ink800,
    accentForeground = Paper,
    border = Color.White.copy(alpha = 0.10f),
    input = Color.White.copy(alpha = 0.15f),
    ring = Color(0xFF79716B),
    destructiveForeground = SurfaceLight,
    sidebar = Ink900,
    sidebarForeground = Paper,
    sidebarAccent = Ink800,
    sidebarAccentForeground = Paper,
    sidebarBorder = Color.White.copy(alpha = 0.10f),
    chart1 = Color(0xFFD6D3D1),
    chart2 = Color(0xFF79716B),
    chart3 = Color(0xFF57534D),
    chart4 = Color(0xFF443F3B),
    chart5 = Color(0xFF292524),
    onboardingAccent = Color(0xFFE5611A),
    onboardingBorder = Color(0xFF3D3833),
    onboardingMuted = Color(0xFF8C8780),
)

internal val LocalAudioColors = staticCompositionLocalOf { DarkAudioColors }
