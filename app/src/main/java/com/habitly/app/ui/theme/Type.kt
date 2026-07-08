package com.habitly.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// PR upload tooling for this environment rejects binary assets, so the app uses
// Android's bundled sans-serif family until real Manrope and Inter font files can
// be attached through a binary-capable channel. The public aliases keep the rest
// of the design system aligned with the spec's display/body font roles.
val Manrope = FontFamily.SansSerif
val Inter = FontFamily.SansSerif

@Immutable
data class HabitlyAppTypography(
    val streakDisplay: TextStyle,
)

val HabitlyTypography = Typography(
    headlineSmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp),
)

val HabitlyExtendedTypography = HabitlyAppTypography(
    streakDisplay = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp, lineHeight = 48.sp),
)
