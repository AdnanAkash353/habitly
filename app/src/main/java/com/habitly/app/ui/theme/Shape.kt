package com.habitly.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class HabitlyRadii(
    val none: Dp = 0.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 20.dp,
    val full: Dp = 999.dp,
)

@Immutable
data class HabitlySpacing(
    val micro: Dp = 4.dp,
    val small: Dp = 8.dp,
    val compact: Dp = 12.dp,
    val standard: Dp = 16.dp,
    val section: Dp = 24.dp,
    val large: Dp = 32.dp,
    val generous: Dp = 48.dp,
)

@Immutable
data class HabitlyElevation(
    val level0: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
    val level4: Dp = 8.dp,
    val level5: Dp = 12.dp,
)

val HabitlyRadius = HabitlyRadii()
val HabitlySpace = HabitlySpacing()
val HabitlyElevations = HabitlyElevation()

val HabitlyShapes = Shapes(
    extraSmall = RoundedCornerShape(HabitlyRadius.xs),
    small = RoundedCornerShape(HabitlyRadius.sm),
    medium = RoundedCornerShape(HabitlyRadius.md),
    large = RoundedCornerShape(HabitlyRadius.lg),
    extraLarge = RoundedCornerShape(HabitlyRadius.lg),
)
