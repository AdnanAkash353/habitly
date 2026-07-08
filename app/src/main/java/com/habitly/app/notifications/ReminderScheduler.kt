package com.habitly.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.habitly.app.data.Habit
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class ReminderScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(habit: Habit) {
        if (!habit.reminderEnabled || habit.reminderHour == null || habit.reminderMinute == null) {
            cancel(habit.id)
            return
        }
        val triggerAt = nextTriggerMillis(habit)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent(habit),
        )
    }

    fun cancel(habitId: Int) {
        alarmManager.cancel(pendingIntent(Habit(id = habitId, name = "", colorHex = "#2F6F5E", frequencyType = "DAILY")))
    }

    private fun pendingIntent(habit: Habit): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_HABIT_ID, habit.id)
            putExtra(ReminderReceiver.EXTRA_HABIT_NAME, habit.name)
            putExtra(ReminderReceiver.EXTRA_COLOR_HEX, habit.colorHex)
            putExtra(ReminderReceiver.EXTRA_FREQUENCY_TYPE, habit.frequencyType)
            putExtra(ReminderReceiver.EXTRA_ACTIVE_DAYS, habit.activeDays)
            putExtra(ReminderReceiver.EXTRA_REMINDER_HOUR, habit.reminderHour ?: 8)
            putExtra(ReminderReceiver.EXTRA_REMINDER_MINUTE, habit.reminderMinute ?: 0)
        }
        return PendingIntent.getBroadcast(
            context,
            habit.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun nextTriggerMillis(habit: Habit): Long {
        val zone = ZoneId.systemDefault()
        val now = LocalDateTime.now(zone)
        var date = LocalDate.now(zone)
        repeat(8) {
            val candidate = date.atTime(habit.reminderHour ?: 8, habit.reminderMinute ?: 0)
            if (candidate.isAfter(now) && habit.isScheduledOn(date)) {
                return candidate.atZone(zone).toInstant().toEpochMilli()
            }
            date = date.plusDays(1)
        }
        return date.atTime(habit.reminderHour ?: 8, habit.reminderMinute ?: 0).atZone(zone).toInstant().toEpochMilli()
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
