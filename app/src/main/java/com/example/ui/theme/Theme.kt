package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val VegasDarkColorScheme = darkColorScheme(
    primary = VegasGoldPrimary,
    onPrimary = VegasBg,
    primaryContainer = VegasGoldContainer,
    onPrimaryContainer = VegasGoldLight,
    secondary = VegasEmerald,
    onSecondary = VegasBg,
    secondaryContainer = VegasEmeraldContainer,
    onSecondaryContainer = VegasEmeraldLight,
    tertiary = VegasCrimson,
    onTertiary = VegasBg,
    tertiaryContainer = VegasCrimsonContainer,
    onTertiaryContainer = VegasCrimsonLight,
    background = VegasBg,
    onBackground = VegasTextPrimary,
    surface = VegasSurface,
    onSurface = VegasTextPrimary,
    surfaceVariant = VegasSurfaceElevated,
    onSurfaceVariant = VegasTextSecondary,
    outline = VegasBorder,
    outlineVariant = VegasBorderSubtle
)

@Composable
fun Vegas50kTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VegasDarkColorScheme,
        typography = Typography,
        content = content
    )
}
