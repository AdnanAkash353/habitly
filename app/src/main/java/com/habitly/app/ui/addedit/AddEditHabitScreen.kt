package com.habitly.app.ui.addedit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitly.app.HabitlyApplication
import com.habitly.app.notifications.ReminderScheduler
import com.habitly.app.ui.theme.HabitlyTheme

@Composable
fun AddEditHabitRoute(
    habitId: Int?,
    onDone: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: AddEditHabitViewModel = viewModel(
        key = "add-edit-${habitId ?: "new"}",
        factory = AddEditHabitViewModel.Factory(app.habitRepository, ReminderScheduler(app), habitId),
    )
    val uiState by viewModel.uiState.collectAsState()
    AddEditHabitScreen(
        uiState = uiState,
        onNameChange = viewModel::updateName,
        onEmojiSelected = viewModel::updateEmoji,
        onColorSelected = viewModel::updateColor,
        onFrequencySelected = viewModel::updateFrequency,
        onToggleDay = viewModel::toggleActiveDay,
        onReminderEnabledChange = viewModel::updateReminderEnabled,
        onSave = { viewModel.save(onDone) },
        onDelete = { viewModel.delete(onDone) },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditHabitScreen(
    uiState: AddEditHabitUiState,
    onNameChange: (String) -> Unit,
    onEmojiSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onFrequencySelected: (String) -> Unit,
    onToggleDay: (Int) -> Unit,
    onReminderEnabledChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
) {
    val spacing = HabitlyTheme.spacing
    var showDeleteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(spacing.standard),
        verticalArrangement = Arrangement.spacedBy(spacing.section),
    ) {
        item {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") },
                isError = uiState.nameError,
                supportingText = {
                    Text(if (uiState.nameError) "Give your habit a name" else "${uiState.name.length}/40")
                },
                singleLine = true,
            )
        }
        item {
            Text("Icon", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(spacing.small))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.small), verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                EmojiPresets.forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(spacing.generous)
                            .clip(CircleShape)
                            .background(if (uiState.emoji == emoji) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(emoji, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
        item {
            Text("Color", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(spacing.small))
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                ColorPresets.forEach { colorHex ->
                    val selected = uiState.colorHex == colorHex
                    Box(
                        Modifier
                            .size(spacing.large)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(colorHex)))
                            .border(if (selected) spacing.micro / 2 else HabitlyTheme.elevation.level0, MaterialTheme.colorScheme.onSurface, CircleShape)
                            .clickable { onColorSelected(colorHex) },
                    )
                }
            }
        }
        item {
            Text("Frequency", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                FilterChip(selected = uiState.frequencyType == FrequencyDaily, onClick = { onFrequencySelected(FrequencyDaily) }, label = { Text("Every day") })
                FilterChip(selected = uiState.frequencyType == FrequencySpecificDays, onClick = { onFrequencySelected(FrequencySpecificDays) }, label = { Text("Specific days") })
            }
            if (uiState.frequencyType == FrequencySpecificDays) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                    listOf(1 to "S", 2 to "M", 3 to "T", 4 to "W", 5 to "T", 6 to "F", 7 to "S").forEach { (day, label) ->
                        FilterChip(selected = day in uiState.activeDays, onClick = { onToggleDay(day) }, label = { Text(label) })
                    }
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("Reminder", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = if (uiState.reminderEnabled) "Time ${uiState.reminderHour ?: 8}:00" else "Off",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = uiState.reminderEnabled, onCheckedChange = onReminderEnabledChange)
            }
        }
        item {
            Button(onClick = onSave, modifier = Modifier.fillMaxWidth(), shape = CircleShape) { Text("Save") }
            if (uiState.isEdit) {
                TextButton(onClick = { showDeleteDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Delete Habit", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete ${uiState.name}?") },
            text = { Text("This also removes its full history. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            },
        )
    }
}
