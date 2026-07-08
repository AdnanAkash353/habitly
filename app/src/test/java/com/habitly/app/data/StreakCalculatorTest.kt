package com.habitly.app.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {
    private val today = LocalDate.of(2026, 7, 8)

    @Test
    fun noCompletionsReturnsZeroes() {
        val result = StreakCalculator.calculate(dailyHabit(), emptySet(), today)

        assertEquals(StreakResult(current = 0, best = 0), result)
    }

    @Test
    fun unbrokenDailyStreakEndingTodayCountsCurrentAndBest() {
        val completions = setOf("2026-07-05", "2026-07-06", "2026-07-07", "2026-07-08")

        val result = StreakCalculator.calculate(dailyHabit(), completions, today)

        assertEquals(StreakResult(current = 4, best = 4), result)
    }

    @Test
    fun dailyHabitCompletedThroughYesterdayStillCountsCurrent() {
        val completions = setOf("2026-07-05", "2026-07-06", "2026-07-07")

        val result = StreakCalculator.calculate(dailyHabit(), completions, today)

        assertEquals(StreakResult(current = 3, best = 3), result)
    }

    @Test
    fun dailyHabitWithGapTracksBestAndRecentCurrentSeparately() {
        val completions = setOf("2026-07-01", "2026-07-02", "2026-07-03", "2026-07-06", "2026-07-07", "2026-07-08")

        val result = StreakCalculator.calculate(dailyHabit(), completions, today)

        assertEquals(StreakResult(current = 3, best = 3), result)
    }

    @Test
    fun specificDaysHabitSkipsUnscheduledDaysBetweenCompletedDays() {
        val habit = specificDaysHabit(activeDays = "2,4,6")
        val completions = setOf("2026-07-06", "2026-07-08")

        val result = StreakCalculator.calculate(habit, completions, today)

        assertEquals(StreakResult(current = 2, best = 2), result)
    }

    @Test
    fun specificDaysHabitBreaksWhenScheduledDayWasMissed() {
        val habit = specificDaysHabit(activeDays = "2,4,6")
        val completions = setOf("2026-07-01", "2026-07-06", "2026-07-08")

        val result = StreakCalculator.calculate(habit, completions, today)

        assertEquals(StreakResult(current = 2, best = 2), result)
    }

    private fun dailyHabit() = Habit(
        id = 1,
        name = "Walk",
        colorHex = "#2F6F5E",
        frequencyType = "DAILY",
    )

    private fun specificDaysHabit(activeDays: String) = Habit(
        id = 2,
        name = "Meditate",
        colorHex = "#8E4A6B",
        frequencyType = "SPECIFIC_DAYS",
        activeDays = activeDays,
    )
}
