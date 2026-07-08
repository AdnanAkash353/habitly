package com.habitly.app.data

import java.time.DayOfWeek
import java.time.LocalDate

data class StreakResult(val current: Int, val best: Int)

object StreakCalculator {
    fun calculate(
        habit: Habit,
        completionDates: Set<String>,
        today: LocalDate = LocalDate.now(),
    ): StreakResult {
        if (completionDates.isEmpty()) return StreakResult(current = 0, best = 0)

        val completions = completionDates.map(LocalDate::parse).toSet()
        return StreakResult(
            current = calculateCurrent(habit, completions, today),
            best = calculateBest(habit, completions),
        )
    }

    private fun calculateCurrent(habit: Habit, completions: Set<LocalDate>, today: LocalDate): Int {
        var cursor = if (isScheduled(habit, today) && today !in completions) {
            today.minusDays(1)
        } else {
            today
        }

        while (!isScheduled(habit, cursor)) {
            cursor = cursor.minusDays(1)
        }

        var current = 0
        while (isScheduled(habit, cursor) || cursor >= completions.minOrNull()) {
            if (!isScheduled(habit, cursor)) {
                cursor = cursor.minusDays(1)
                continue
            }
            if (cursor !in completions) break
            current++
            cursor = cursor.minusDays(1)
        }
        return current
    }

    private fun calculateBest(habit: Habit, completions: Set<LocalDate>): Int {
        val first = completions.minOrNull() ?: return 0
        val last = completions.maxOrNull() ?: return 0
        var cursor = first
        var running = 0
        var best = 0
        while (!cursor.isAfter(last)) {
            if (isScheduled(habit, cursor)) {
                if (cursor in completions) {
                    running++
                    best = maxOf(best, running)
                } else {
                    running = 0
                }
            }
            cursor = cursor.plusDays(1)
        }
        return best
    }

    private fun isScheduled(habit: Habit, date: LocalDate): Boolean = when (habit.frequencyType) {
        "DAILY" -> true
        "SPECIFIC_DAYS" -> dayNumber(date.dayOfWeek) in habit.activeDays.toDayNumbers()
        else -> true
    }

    private fun String.toDayNumbers(): Set<Int> = split(',')
        .mapNotNull { it.trim().toIntOrNull() }
        .filter { it in 1..7 }
        .toSet()

    private fun dayNumber(dayOfWeek: DayOfWeek): Int = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
        DayOfWeek.SATURDAY -> 7
    }
}
