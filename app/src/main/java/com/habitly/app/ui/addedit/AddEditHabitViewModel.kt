package com.habitly.app.ui.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habitly.app.data.Habit
import com.habitly.app.data.HabitRepository
import com.habitly.app.notifications.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddEditHabitViewModel(
    private val repository: HabitRepository,
    private val reminderScheduler: ReminderScheduler,
    private val habitId: Int?,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditHabitUiState(isEdit = habitId != null))
    val uiState: StateFlow<AddEditHabitUiState> = _uiState

    init {
        if (habitId != null) {
            viewModelScope.launch {
                repository.observeHabit(habitId).collect { habit ->
                    if (habit != null && !_uiState.value.hasLoadedHabit) {
                        _uiState.value = AddEditHabitUiState.fromHabit(habit)
                    }
                }
            }
        }
    }

    fun updateName(name: String) {
        if (name.length <= 40) {
            _uiState.update { it.copy(name = name, nameError = false) }
        }
    }

    fun updateEmoji(emoji: String) = _uiState.update { it.copy(emoji = emoji) }

    fun updateColor(colorHex: String) = _uiState.update { it.copy(colorHex = colorHex) }

    fun updateFrequency(frequencyType: String) = _uiState.update { it.copy(frequencyType = frequencyType) }

    fun toggleActiveDay(day: Int) = _uiState.update { state ->
        val days = if (day in state.activeDays) state.activeDays - day else state.activeDays + day
        state.copy(activeDays = days.sorted().toSet())
    }

    fun updateReminderEnabled(enabled: Boolean) = _uiState.update { it.copy(reminderEnabled = enabled) }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = true) }
            return
        }
        viewModelScope.launch {
            val existingId = habitId ?: 0
            val sortOrder = state.sortOrder ?: repository.nextSortOrder()
            val habit = Habit(
                id = existingId,
                name = state.name.trim(),
                emoji = state.emoji,
                colorHex = state.colorHex,
                frequencyType = state.frequencyType,
                activeDays = if (state.frequencyType == FrequencySpecificDays) state.activeDays.joinToString(",") else "",
                reminderEnabled = state.reminderEnabled,
                reminderHour = if (state.reminderEnabled) state.reminderHour else null,
                reminderMinute = if (state.reminderEnabled) state.reminderMinute else null,
                createdAt = state.createdAt ?: System.currentTimeMillis(),
                sortOrder = sortOrder,
            )
            if (habitId == null) {
                val newId = repository.createHabit(habit).toInt()
                reminderScheduler.schedule(habit.copy(id = newId))
            } else {
                repository.updateHabit(habit)
                reminderScheduler.schedule(habit)
            }
            onSaved()
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val state = _uiState.value
        val id = habitId ?: return
        viewModelScope.launch {
            reminderScheduler.cancel(id)
            repository.deleteHabit(
                Habit(
                    id = id,
                    name = state.name,
                    emoji = state.emoji,
                    colorHex = state.colorHex,
                    frequencyType = state.frequencyType,
                    activeDays = state.activeDays.joinToString(","),
                    reminderEnabled = state.reminderEnabled,
                    reminderHour = state.reminderHour,
                    reminderMinute = state.reminderMinute,
                    createdAt = state.createdAt ?: System.currentTimeMillis(),
                    sortOrder = state.sortOrder ?: 0,
                ),
            )
            onDeleted()
        }
    }

    class Factory(
        private val repository: HabitRepository,
        private val reminderScheduler: ReminderScheduler,
        private val habitId: Int?,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AddEditHabitViewModel(repository, reminderScheduler, habitId) as T
    }
}

const val FrequencyDaily = "DAILY"
const val FrequencySpecificDays = "SPECIFIC_DAYS"

val EmojiPresets = listOf("🌱", "💧", "📚", "🏃", "🧘", "😴", "🍎", "💪", "🎨", "🎯", "☀️", "🧹", "💰", "✍️", "🎵", "🚭", "🥗", "🧠", "🙏", "⭐")
val ColorPresets = listOf("#2F6F5E", "#E8A33D", "#6B8E4E", "#4A7FA6", "#C4653A", "#8E4A6B", "#5B6470", "#5C5EA6")

data class AddEditHabitUiState(
    val isEdit: Boolean = false,
    val hasLoadedHabit: Boolean = false,
    val name: String = "",
    val nameError: Boolean = false,
    val emoji: String = "✅",
    val colorHex: String = ColorPresets.first(),
    val frequencyType: String = FrequencyDaily,
    val activeDays: Set<Int> = setOf(2, 3, 4, 5, 6),
    val reminderEnabled: Boolean = false,
    val reminderHour: Int? = 8,
    val reminderMinute: Int? = 0,
    val createdAt: Long? = null,
    val sortOrder: Int? = null,
) {
    companion object {
        fun fromHabit(habit: Habit) = AddEditHabitUiState(
            isEdit = true,
            hasLoadedHabit = true,
            name = habit.name,
            emoji = habit.emoji,
            colorHex = habit.colorHex,
            frequencyType = habit.frequencyType,
            activeDays = habit.activeDays.split(',').mapNotNull { it.toIntOrNull() }.toSet(),
            reminderEnabled = habit.reminderEnabled,
            reminderHour = habit.reminderHour,
            reminderMinute = habit.reminderMinute,
            createdAt = habit.createdAt,
            sortOrder = habit.sortOrder,
        )
    }
}
