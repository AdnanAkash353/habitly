package com.habitly.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitly.app.ui.theme.HabitlyTheme

@Composable
fun HomeRoute(
    onAddHabit: () -> Unit,
    onOpenHabit: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    HomeScreen(
        uiState = uiState,
        onAddHabit = onAddHabit,
        onOpenHabit = onOpenHabit,
        onToggleHabit = viewModel::toggleToday,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAddHabit: () -> Unit,
    onOpenHabit: (Int) -> Unit,
    onToggleHabit: (habitId: Int, completed: Boolean) -> Unit,
) {
    val spacing = HabitlyTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(spacing.standard),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        item {
            Text("Today", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "${uiState.todayLabel}'s habits",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(spacing.compact))
        }

        if (uiState.habits.isEmpty()) {
            item {
                EmptyHomeState(hasAnyHabits = uiState.hasAnyHabits)
            }
        } else {
            items(uiState.habits, key = { it.id }) { habit ->
                HomeHabitCard(
                    habit = habit,
                    onToggle = { onToggleHabit(habit.id, habit.isCompletedToday) },
                    onOpen = { onOpenHabit(habit.id) },
                )
            }
        }

        item {
            Spacer(Modifier.height(spacing.section))
            Button(onClick = onAddHabit, modifier = Modifier.fillMaxWidth(), shape = CircleShape) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(Modifier.width(spacing.small))
                Text("Add Habit")
            }
        }
    }
}

@Composable
private fun EmptyHomeState(hasAnyHabits: Boolean) {
    val spacing = HabitlyTheme.spacing
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = spacing.generous),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        Text(
            text = if (hasAnyHabits) "Nothing scheduled for today — enjoy the day off." else "No habits yet",
            style = MaterialTheme.typography.headlineSmall,
        )
        if (!hasAnyHabits) {
            Text(
                text = "Add your first one to start building a streak.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomeHabitCard(
    habit: HomeHabitUiState,
    onToggle: () -> Unit,
    onOpen: () -> Unit,
) {
    val spacing = HabitlyTheme.spacing
    Card(
        onClick = onOpen,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = HabitlyTheme.elevation.level1),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.alpha(if (habit.isCompletedToday) 0.6f else 1f),
    ) {
        Row(Modifier.fillMaxWidth().padding(spacing.standard), verticalAlignment = Alignment.CenterVertically) {
            EmojiChip(habit)
            Spacer(Modifier.width(spacing.standard))
            Column(Modifier.weight(1f)) {
                Text(habit.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "🍃 ${habit.currentStreak} day streak",
                    style = MaterialTheme.typography.labelMedium,
                    color = HabitlyTheme.colors.streakAccent,
                )
            }
            Box(
                Modifier
                    .size(spacing.generous)
                    .clip(CircleShape)
                    .background(if (habit.isCompletedToday) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .border(spacing.micro / 2, MaterialTheme.colorScheme.outline, CircleShape)
                    .clickable(onClick = onToggle)
                    .semantics {
                        contentDescription = if (habit.isCompletedToday) {
                            "Mark ${habit.name} incomplete for today"
                        } else {
                            "Mark ${habit.name} complete for today"
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (habit.isCompletedToday) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
private fun EmojiChip(habit: HomeHabitUiState) {
    val color = Color(android.graphics.Color.parseColor(habit.colorHex))
    Box(
        modifier = Modifier
            .size(HabitlyTheme.spacing.generous)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(habit.emoji, style = MaterialTheme.typography.titleLarge)
    }
}
