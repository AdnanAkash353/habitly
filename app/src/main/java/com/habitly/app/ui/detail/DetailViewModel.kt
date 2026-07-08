package com.habitly.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habitly.app.data.Habit
import com.habitly.app.data.HabitRepository
import com.habitly.app.data.StreakCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class DetailViewModel(
    private val repository: HabitRepository,
    private val habitId: Int,
) : ViewModel() {
    private val today = LocalDate.now()

    val uiState: StateFlow<DetailUiState> = combine(
        repository.observeHabit(habitId),
        repository.observeCompletionsForHabit(habitId),
    ) { habit, completions ->
        if (habit == null) {
            DetailUiState.NotFound
        } else {
            val completionDates = completions.map { it.date }.toSet()
            val streak = StreakCalculator.calculate(habit, completionDates, today)
            DetailUiState.Loaded(
                habitId = habit.id,
                name = habit.name,
                emoji = habit.emoji,
                colorHex = habit.colorHex,
                currentStreak = streak.current,
                bestStreak = streak.best,
                heatmapDays = buildHeatmap(completionDates),
                hasCompletions = completionDates.isNotEmpty(),
                habit = habit,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailUiState.Loading,
    )

    fun deleteHabit(onDeleted: () -> Unit) {
        val loaded = uiState.value as? DetailUiState.Loaded ?: return
        viewModelScope.launch {
            repository.deleteHabit(loaded.habit)
            onDeleted()
        }
    }

    private fun buildHeatmap(completionDates: Set<String>): List<DetailHeatmapDay> {
        val start = today.minusWeeks(11).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        return (0 until 84).map { offset ->
            val date = start.plusDays(offset.toLong())
            DetailHeatmapDay(
                date = date,
                completed = date.toString() in completionDates,
                future = date.isAfter(today),
                today = date == today,
            )
        }
    }

    class Factory(
        private val repository: HabitRepository,
        private val habitId: Int,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = DetailViewModel(repository, habitId) as T
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data object NotFound : DetailUiState

    data class Loaded(
        val habitId: Int,
        val name: String,
        val emoji: String,
        val colorHex: String,
        val currentStreak: Int,
        val bestStreak: Int,
        val heatmapDays: List<DetailHeatmapDay>,
        val hasCompletions: Boolean,
        val habit: Habit,
    ) : DetailUiState
}

data class DetailHeatmapDay(
    val date: LocalDate,
    val completed: Boolean,
    val future: Boolean,
    val today: Boolean,
)
