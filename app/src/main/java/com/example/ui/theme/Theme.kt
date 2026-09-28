package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = EmeraldDark,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = EmeraldDark,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = GoldLight,
    tertiary = GoldBright,
    onTertiary = EmeraldDark,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorderLine
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldMedium,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = GoldLight,
    secondary = GoldAccent,
    onSecondary = EmeraldDark,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = EmeraldDark,
    tertiary = EmeraldLight,
    onTertiary = Color.White,
    background = CreamBackground,
    onBackground = InkTextPrimary,
    surface = PaperCard,
    onSurface = InkTextPrimary,
    surfaceVariant = SurfaceVariantWarm,
    onSurfaceVariant = InkTextSecondary,
    outline = BorderLine
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
