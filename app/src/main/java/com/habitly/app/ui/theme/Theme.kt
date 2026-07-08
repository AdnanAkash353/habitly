package com.habitly.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = HabitlyPrimaryLight,
    onPrimary = HabitlyOnPrimaryLight,
    primaryContainer = HabitlyPrimaryContainerLight,
    onPrimaryContainer = HabitlyOnPrimaryContainerLight,
    secondary = HabitlySecondaryLight,
    onSecondary = HabitlyOnSecondaryLight,
    secondaryContainer = HabitlySecondaryContainerLight,
    onSecondaryContainer = HabitlyOnSecondaryContainerLight,
    background = HabitlyBackgroundLight,
    onBackground = HabitlyOnBackgroundLight,
    surface = HabitlySurfaceLight,
    onSurface = HabitlyOnSurfaceLight,
    surfaceVariant = HabitlySurfaceVariantLight,
    onSurfaceVariant = HabitlyOnSurfaceVariantLight,
    outline = HabitlyOutlineLight,
    outlineVariant = HabitlyOutlineVariantLight,
    error = HabitlyErrorLight,
    onError = HabitlyOnErrorLight,
    errorContainer = HabitlyErrorContainerLight,
    onErrorContainer = HabitlyOnErrorContainerLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = HabitlyPrimaryDark,
    onPrimary = HabitlyOnPrimaryDark,
    primaryContainer = HabitlyPrimaryContainerDark,
    onPrimaryContainer = HabitlyOnPrimaryContainerDark,
    secondary = HabitlySecondaryDark,
    onSecondary = HabitlyOnSecondaryDark,
    secondaryContainer = HabitlySecondaryContainerDark,
    onSecondaryContainer = HabitlyOnSecondaryContainerDark,
    background = HabitlyBackgroundDark,
    onBackground = HabitlyOnBackgroundDark,
    surface = HabitlySurfaceDark,
    onSurface = HabitlyOnSurfaceDark,
    surfaceVariant = HabitlySurfaceVariantDark,
    onSurfaceVariant = HabitlyOnSurfaceVariantDark,
    outline = HabitlyOutlineDark,
    outlineVariant = HabitlyOutlineVariantDark,
    error = HabitlyErrorDark,
    onError = HabitlyOnErrorDark,
    errorContainer = HabitlyErrorContainerDark,
    onErrorContainer = HabitlyOnErrorContainerDark,
)

val LocalHabitlyTypography = staticCompositionLocalOf { HabitlyExtendedTypography }
val LocalHabitlySpacing = staticCompositionLocalOf { HabitlySpace }
val LocalHabitlyRadii = staticCompositionLocalOf { HabitlyRadius }
val LocalHabitlyElevation = staticCompositionLocalOf { HabitlyElevations }
val LocalHabitlyExtras = staticCompositionLocalOf { HabitlyExtraColors(false) }

data class HabitlyExtraColors(
    val isDark: Boolean,
    val streakAccent: Color = if (isDark) HabitlySecondaryDark else HabitlyStreakAccentLight,
    val heatmapFill: Color = if (isDark) HabitlyHeatmapFillDark else HabitlyHeatmapFillLight,
    val todayRingFilled: Color = HabitlyTodayRingFilled,
)

object HabitlyTheme {
    val typography: HabitlyAppTypography
        @Composable get() = LocalHabitlyTypography.current
    val spacing: HabitlySpacing
        @Composable get() = LocalHabitlySpacing.current
    val radii: HabitlyRadii
        @Composable get() = LocalHabitlyRadii.current
    val elevation: HabitlyElevation
        @Composable get() = LocalHabitlyElevation.current
    val colors: HabitlyExtraColors
        @Composable get() = LocalHabitlyExtras.current
}

@Composable
fun HabitlyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme: ColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    androidx.compose.runtime.CompositionLocalProvider(
        LocalHabitlyTypography provides HabitlyExtendedTypography,
        LocalHabitlySpacing provides HabitlySpace,
        LocalHabitlyRadii provides HabitlyRadius,
        LocalHabitlyElevation provides HabitlyElevations,
        LocalHabitlyExtras provides HabitlyExtraColors(darkTheme),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = HabitlyTypography,
            shapes = HabitlyShapes,
            content = content,
        )
    }
}
