package com.habitly.app.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.habitly.app.HabitlyApplication
import com.habitly.app.data.Habit
import com.habitly.app.data.HabitCompletion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as HabitlyApplication).habitRepository
    private val today = LocalDate.now()

    val uiState: StateFlow<StatsUiState> = combine(
        repository.observeHabits(),
        repository.observeAllCompletions(),
    ) { habits, completions ->
        val completionsByHabit = completions.groupBy { it.habitId }
        val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val monthStart = today.withDayOfMonth(1)
        val hasCompletions = completions.isNotEmpty()

        StatsUiState(
            isEmpty = habits.isEmpty() || !hasCompletions,
            weekPercentage = aggregatePercentage(habits, completionsByHabit, weekStart, today),
            monthPercentage = aggregatePercentage(habits, completionsByHabit, monthStart, today),
            habits = habits.map { habit ->
                HabitStatsUiState(
                    id = habit.id,
                    name = habit.name,
                    emoji = habit.emoji,
                    colorHex = habit.colorHex,
                    percentage = habitPercentage(habit, completionsByHabit[habit.id].orEmpty(), monthStart, today),
                )
            },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatsUiState(),
    )

    private fun aggregatePercentage(
        habits: List<Habit>,
        completionsByHabit: Map<Int, List<HabitCompletion>>,
        start: LocalDate,
        end: LocalDate,
    ): Int {
        val opportunities = habits.sumOf { habit -> scheduledDates(habit, start, end).size }
        if (opportunities == 0) return 0
        val completed = habits.sumOf { habit ->
            val dates = completionsByHabit[habit.id].orEmpty().map { it.date }.toSet()
            scheduledDates(habit, start, end).count { it.toString() in dates }
        }
        return ((completed.toFloat() / opportunities) * 100).toInt()
    }

    private fun habitPercentage(habit: Habit, completions: List<HabitCompletion>, start: LocalDate, end: LocalDate): Int {
        val scheduled = scheduledDates(habit, start, end)
        if (scheduled.isEmpty()) return 0
        val dates = completions.map { it.date }.toSet()
        return ((scheduled.count { it.toString() in dates }.toFloat() / scheduled.size) * 100).toInt()
    }

    private fun scheduledDates(habit: Habit, start: LocalDate, end: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var cursor = start
        while (!cursor.isAfter(end)) {
            if (habit.isScheduledOn(cursor)) dates += cursor
            cursor = cursor.plusDays(1)
        }
        return dates
    }

    private fun Habit.isScheduledOn(date: LocalDate): Boolean = when (frequencyType) {
        "DAILY" -> true
        "SPECIFIC_DAYS" -> date.dayOfWeek.toSpecNumber() in activeDays.toSpecNumbers()
        else -> true
    }

    private fun String.toSpecNumbers(): Set<Int> = split(',')
        .mapNotNull { it.trim().toIntOrNull() }
        .filter { it in 1..7 }
        .toSet()

    private fun DayOfWeek.toSpecNumber(): Int = when (this) {
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
        DayOfWeek.SATURDAY -> 7
    }
}

data class StatsUiState(
    val isEmpty: Boolean = true,
    val weekPercentage: Int = 0,
    val monthPercentage: Int = 0,
    val habits: List<HabitStatsUiState> = emptyList(),
)

data class HabitStatsUiState(
    val id: Int,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val percentage: Int,
)
