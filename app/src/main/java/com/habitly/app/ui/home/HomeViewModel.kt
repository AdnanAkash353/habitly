package com.habitly.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.habitly.app.HabitlyApplication
import com.habitly.app.data.Habit
import com.habitly.app.data.HabitCompletion
import com.habitly.app.data.StreakCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as HabitlyApplication).habitRepository
    private val today: LocalDate = LocalDate.now()
    private val todayIso: String = today.toString()

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeHabits(),
        repository.observeAllCompletions(),
    ) { habits, completions ->
        val completionsByHabit = completions.groupBy { it.habitId }
        val scheduledToday = habits.filter { it.isScheduledOn(today) }
        HomeUiState(
            hasAnyHabits = habits.isNotEmpty(),
            todayLabel = today.dayOfWeek.name.lowercase().replaceFirstChar { it.titlecase() },
            habits = scheduledToday.map { habit ->
                val habitCompletions = completionsByHabit[habit.id].orEmpty()
                val completionDates = habitCompletions.map(HabitCompletion::date).toSet()
                HomeHabitUiState(
                    id = habit.id,
                    name = habit.name,
                    emoji = habit.emoji,
                    colorHex = habit.colorHex,
                    currentStreak = StreakCalculator.calculate(habit, completionDates, today).current,
                    isCompletedToday = todayIso in completionDates,
                )
            },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    fun toggleToday(habitId: Int, completed: Boolean) {
        viewModelScope.launch {
            if (completed) {
                repository.markIncomplete(habitId, todayIso)
            } else {
                repository.markComplete(habitId, todayIso)
            }
        }
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

data class HomeUiState(
    val hasAnyHabits: Boolean = false,
    val todayLabel: String = "Today",
    val habits: List<HomeHabitUiState> = emptyList(),
)

data class HomeHabitUiState(
    val id: Int,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val currentStreak: Int,
    val isCompletedToday: Boolean,
)
