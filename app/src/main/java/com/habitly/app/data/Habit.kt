package com.habitly.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val emoji: String = "✅",
    val colorHex: String,
    val frequencyType: String,
    val activeDays: String = "",
    val reminderEnabled: Boolean = false,
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0,
)
