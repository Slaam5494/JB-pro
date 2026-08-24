package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LuminaDarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = VoidDark,
    primaryContainer = DarkCardSurface,
    onPrimaryContainer = CyanPrimary,
    secondary = PurplePrimary,
    onSecondary = VoidDark,
    secondaryContainer = DarkCardSecondary,
    onSecondaryContainer = PurplePrimary,
    tertiary = EmeraldAccent,
    onTertiary = VoidDark,
    background = VoidDark,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderCyan,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun LuminaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LuminaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
