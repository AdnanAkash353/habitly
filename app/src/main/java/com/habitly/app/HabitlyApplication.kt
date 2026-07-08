package com.habitly.app

import android.app.Application
import com.habitly.app.data.AppDatabase
import com.habitly.app.data.HabitRepository
import com.habitly.app.data.PreferencesRepository

class HabitlyApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val habitRepository: HabitRepository by lazy {
        HabitRepository(
            habitDao = database.habitDao(),
            completionDao = database.habitCompletionDao(),
        )
    }
    val preferencesRepository: PreferencesRepository by lazy { PreferencesRepository(this) }
}
