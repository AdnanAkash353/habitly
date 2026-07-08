package com.habitly.app.data

import kotlinx.coroutines.flow.Flow

class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao,
) {
    fun observeHabits(): Flow<List<Habit>> = habitDao.observeAll()

    fun observeHabit(habitId: Int): Flow<Habit?> = habitDao.observeById(habitId)

    fun observeAllCompletions(): Flow<List<HabitCompletion>> = completionDao.observeAll()

    fun observeCompletionsForHabit(habitId: Int): Flow<List<HabitCompletion>> =
        completionDao.observeForHabit(habitId)

    fun observeCompletionsForDate(date: String): Flow<List<HabitCompletion>> =
        completionDao.observeForDate(date)

    fun observeIsCompleted(habitId: Int, date: String): Flow<Boolean> =
        completionDao.observeIsCompleted(habitId, date)

    suspend fun nextSortOrder(): Int = habitDao.nextSortOrder()

    suspend fun createHabit(habit: Habit): Long = habitDao.insert(habit)

    suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.delete(habit)

    suspend fun markComplete(habitId: Int, date: String): Long = completionDao.insert(
        HabitCompletion(habitId = habitId, date = date),
    )

    suspend fun markIncomplete(habitId: Int, date: String) =
        completionDao.deleteForHabitAndDate(habitId, date)
}
