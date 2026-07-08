package com.habitly.app.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitly.app.HabitlyApplication
import com.habitly.app.ui.theme.HabitlyTheme
import java.time.format.DateTimeFormatter

@Composable
fun DetailRoute(
    habitId: Int,
    onEdit: (Int) -> Unit,
    onDeleted: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: DetailViewModel = viewModel(
        key = "detail-$habitId",
        factory = DetailViewModel.Factory(app.habitRepository, habitId),
    )
    val uiState by viewModel.uiState.collectAsState()
    DetailScreen(
        uiState = uiState,
        onEdit = onEdit,
        onDelete = { viewModel.deleteHabit(onDeleted) },
    )
}

@Composable
fun DetailScreen(
    uiState: DetailUiState,
    onEdit: (Int) -> Unit,
    onDelete: () -> Unit,
) {
    when (uiState) {
        DetailUiState.Loading -> Box(Modifier.fillMaxSize())
        DetailUiState.NotFound -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Habit not found", style = MaterialTheme.typography.headlineSmall)
        }
        is DetailUiState.Loaded -> LoadedDetail(uiState, onEdit, onDelete)
    }
}

@Composable
private fun LoadedDetail(
    state: DetailUiState.Loaded,
    onEdit: (Int) -> Unit,
    onDelete: () -> Unit,
) {
    val spacing = HabitlyTheme.spacing
    var showDeleteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(spacing.standard),
        verticalArrangement = Arrangement.spacedBy(spacing.section),
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                EmojiChip(state)
                Text(state.name, style = MaterialTheme.typography.headlineSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                    TextButton(onClick = { onEdit(state.habitId) }) {
                        Icon(Icons.Outlined.Edit, contentDescription = null)
                        Text("Edit")
                    }
                    TextButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                StreakStat("Current streak", state.currentStreak, Modifier.weight(1f))
                StreakStat("Best streak", state.bestStreak, Modifier.weight(1f))
            }
        }
        item { HeatmapCard(state.heatmapDays, state.hasCompletions) }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete ${state.name}?") },
            text = { Text("This also removes its full history. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun EmojiChip(state: DetailUiState.Loaded) {
    val color = Color(android.graphics.Color.parseColor(state.colorHex))
    Box(
        modifier = Modifier.size(HabitlyTheme.spacing.generous).clip(CircleShape).background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) { Text(state.emoji, style = MaterialTheme.typography.titleLarge) }
}

@Composable
private fun StreakStat(label: String, value: Int, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(HabitlyTheme.spacing.standard), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value.toString(), style = HabitlyTheme.typography.streakDisplay, color = HabitlyTheme.colors.streakAccent)
            Text("🍃", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun HeatmapCard(days: List<DetailHeatmapDay>, hasCompletions: Boolean) {
    val spacing = HabitlyTheme.spacing
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(spacing.standard)) {
            Text("Last 12 weeks", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(spacing.compact))
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.micro)) {
                days.chunked(7).forEach { week ->
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.micro)) {
                        week.forEach { day -> HeatmapCell(day) }
                    }
                }
            }
            Spacer(Modifier.height(spacing.compact))
            Text("Less   More", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!hasCompletions) {
                Spacer(Modifier.height(spacing.small))
                Text("Mark today complete on Home to start your streak.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun HeatmapCell(day: DetailHeatmapDay) {
    val formatter = DateTimeFormatter.ofPattern("MMMM d")
    val outline = if (day.today) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline
    Box(
        Modifier
            .size(HabitlyTheme.spacing.compact)
            .background(if (day.completed) HabitlyTheme.colors.heatmapFill else Color.Transparent, MaterialTheme.shapes.extraSmall)
            .border(if (day.today) HabitlyTheme.spacing.micro / 2 else HabitlyTheme.spacing.micro / 4, outline.copy(alpha = if (day.future) 0.38f else 1f), MaterialTheme.shapes.extraSmall)
            .semantics { contentDescription = "${day.date.format(formatter)}, ${if (day.completed) "completed" else "not completed"}" },
    )
}
