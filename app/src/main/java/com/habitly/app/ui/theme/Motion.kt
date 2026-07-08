package com.habitly.app.ui.theme

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing

object HabitlyMotion {
    const val MicroDurationMillis = 100
    const val SmallDurationMillis = 200
    const val StandardDurationMillis = 300
    const val EmphasizedDurationMillis = 400
    const val StatsSweepDurationMillis = 500
    const val HeatmapCellStaggerMillis = 7
    const val HomeRowStaggerMillis = 30

    val StandardEasing = FastOutSlowInEasing
    val EnterEasing = LinearOutSlowInEasing
    val ExitEasing = FastOutLinearInEasing
}
