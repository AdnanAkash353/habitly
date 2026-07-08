package com.habitly.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.habitly.app.R
import com.habitly.app.data.Habit

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        ensureChannel(context)
        val habitId = intent.getIntExtra(EXTRA_HABIT_ID, 0)
        val habitName = intent.getStringExtra(EXTRA_HABIT_NAME).orEmpty().ifBlank { "your habit" }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Habitly reminder")
            .setContentText("Time for $habitName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(habitId, notification)
        ReminderScheduler(context).schedule(
            Habit(
                id = habitId,
                name = habitName,
                colorHex = intent.getStringExtra(EXTRA_COLOR_HEX) ?: "#2F6F5E",
                frequencyType = intent.getStringExtra(EXTRA_FREQUENCY_TYPE) ?: "DAILY",
                activeDays = intent.getStringExtra(EXTRA_ACTIVE_DAYS).orEmpty(),
                reminderEnabled = true,
                reminderHour = intent.getIntExtra(EXTRA_REMINDER_HOUR, 8),
                reminderMinute = intent.getIntExtra(EXTRA_REMINDER_MINUTE, 0),
            ),
        )
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Habit reminders", NotificationManager.IMPORTANCE_DEFAULT)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        const val EXTRA_HABIT_ID = "habit_id"
        const val EXTRA_HABIT_NAME = "habit_name"
        const val EXTRA_COLOR_HEX = "color_hex"
        const val EXTRA_FREQUENCY_TYPE = "frequency_type"
        const val EXTRA_ACTIVE_DAYS = "active_days"
        const val EXTRA_REMINDER_HOUR = "reminder_hour"
        const val EXTRA_REMINDER_MINUTE = "reminder_minute"
        private const val CHANNEL_ID = "habit_reminders"
    }
}
