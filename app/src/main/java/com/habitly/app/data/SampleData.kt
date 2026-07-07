package com.habitly.app.data

import java.time.DayOfWeek
import java.time.LocalDate

const val DAILY = "DAILY"
const val SPECIFIC_DAYS = "SPECIFIC_DAYS"

data class SampleHabit(
    val id: Int,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val frequencyType: String,
    val activeDays: Set<DayOfWeek> = emptySet(),
    val currentStreak: Int,
    val bestStreak: Int,
    val completionRate: Int,
)

data class HeatmapDay(val date: LocalDate, val completed: Boolean, val future: Boolean = false)

object SampleData {
    val habits = listOf(
        SampleHabit(1, "Morning walk", "🏃", "#2F6F5E", DAILY, currentStreak = 8, bestStreak = 21, completionRate = 86),
        SampleHabit(2, "Read ten pages", "📚", "#4A7FA6", DAILY, currentStreak = 4, bestStreak = 14, completionRate = 72),
        SampleHabit(3, "Meditate", "🧘", "#8E4A6B", SPECIFIC_DAYS, setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY), 6, 18, 68),
        SampleHabit(4, "Water plants", "🌱", "#6B8E4E", SPECIFIC_DAYS, setOf(DayOfWeek.TUESDAY, DayOfWeek.SATURDAY), 3, 11, 61),
        SampleHabit(5, "No smoking", "🚭", "#E8A33D", DAILY, currentStreak = 15, bestStreak = 37, completionRate = 93),
    )

    val completedTodayHabitIds = setOf(1, 3)

    fun heatmapDays(today: LocalDate = LocalDate.now()): List<HeatmapDay> {
        val start = today.minusWeeks(11).with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        return (0 until 84).map { offset ->
            val date = start.plusDays(offset.toLong())
            HeatmapDay(
                date = date,
                completed = !date.isAfter(today) && (offset % 3 == 0 || offset % 11 == 0 || offset % 17 == 0),
                future = date.isAfter(today),
            )
        }
    }
}
