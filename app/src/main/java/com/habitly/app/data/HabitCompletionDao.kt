package com.habitly.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(completion: HabitCompletion): Long

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteForHabitAndDate(habitId: Int, date: String)

    @Query("SELECT * FROM habit_completions ORDER BY date ASC")
    fun observeAll(): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY date ASC")
    fun observeForHabit(habitId: Int): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun observeForDate(date: String): Flow<List<HabitCompletion>>

    @Query("SELECT EXISTS(SELECT 1 FROM habit_completions WHERE habitId = :habitId AND date = :date)")
    fun observeIsCompleted(habitId: Int, date: String): Flow<Boolean>
}
