package dev.nsomnia.chadsuno.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Chad dark theme colors - Terminal/Hacker aesthetic
val TerminalBlack = Color(0xFF0D0D0D)
val TerminalDarker = Color(0xFF080808)
val NeonGreen = Color(0xFF00FF41)
val NeonGreenDim = Color(0xFF00CC33)
val NeonCyan = Color(0xFF00FFFF)
val NeonMagenta = Color(0xFFFF00FF)
val NeonPurple = Color(0xFF9D00FF)
val MatrixGreen = Color(0xFF003B00)
val SlateGray = Color(0xFF2D2D2D)
val SlateGrayLight = Color(0xFF3D3D3D)
val AccentBlue = Color(0xFF00D4FF)
val AccentPink = Color(0xFFFF0080)
val ErrorRed = Color(0xFFFF3333)
val WarningYellow = Color(0xFFFFFF00)

val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = TerminalBlack,
    primaryContainer = MatrixGreen,
    onPrimaryContainer = NeonGreen,
    secondary = NeonCyan,
    onSecondary = TerminalBlack,
    secondaryContainer = Color(0xFF003333),
    onSecondaryContainer = NeonCyan,
    tertiary = NeonMagenta,
    onTertiary = TerminalBlack,
    tertiaryContainer = Color(0xFF330033),
    onTertiaryContainer = NeonMagenta,
    background = TerminalBlack,
    onBackground = Color(0xFFE0E0E0),
    surface = SlateGray,
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = SlateGrayLight,
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF505050),
    outlineVariant = Color(0xFF404040),
    error = ErrorRed,
    onError = TerminalBlack,
    errorContainer = Color(0xFF330000),
    onErrorContainer = ErrorRed,
    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = TerminalBlack,
    inversePrimary = Color(0xFF003300),
    scrim = TerminalDarker
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006B00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8FFB8),
    onPrimaryContainer = Color(0xFF002200),
    secondary = Color(0xFF006666),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB8FFFF),
    onSecondaryContainer = Color(0xFF002222),
    tertiary = Color(0xFF660066),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFB8FF),
    onTertiaryContainer = Color(0xFF220022),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF505050),
    outline = Color(0xFF909090),
    error = Color(0xFFCC0000),
    onError = Color.White,
    errorContainer = Color(0xFFFFE0E0),
    onErrorContainer = Color(0xFF660000)
)
