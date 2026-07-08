package com.habitly.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert
    suspend fun insert(habit: Habit): Long

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("SELECT * FROM habits ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun observeById(habitId: Int): Flow<Habit?>

    @Query("SELECT COALESCE(MAX(sortOrder) + 1, 0) FROM habits")
    suspend fun nextSortOrder(): Int
}
