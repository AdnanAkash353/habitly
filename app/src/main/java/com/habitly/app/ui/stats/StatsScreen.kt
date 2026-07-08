package com.habitly.app.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitly.app.ui.theme.HabitlyTheme

@Composable
fun StatsRoute(viewModel: StatsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    StatsScreen(uiState)
}

@Composable
fun StatsScreen(uiState: StatsUiState) {
    val spacing = HabitlyTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(spacing.standard),
        verticalArrangement = Arrangement.spacedBy(spacing.section),
    ) {
        if (uiState.isEmpty) {
            item { EmptyStatsState() }
        } else {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                    ProgressCard("This week", uiState.weekPercentage, Modifier.weight(1f))
                    ProgressCard("This month", uiState.monthPercentage, Modifier.weight(1f))
                }
            }
            item { Text("By Habit", style = MaterialTheme.typography.titleLarge) }
            items(uiState.habits, key = { it.id }) { habit -> HabitProgressRow(habit) }
        }
    }
}

@Composable
private fun EmptyStatsState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = HabitlyTheme.spacing.generous),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HabitlyTheme.spacing.small),
    ) {
        Text("Nothing to show yet", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Complete a few habits and your stats will show up here.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ProgressCard(label: String, percentage: Int, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(HabitlyTheme.spacing.standard), horizontalAlignment = Alignment.CenterHorizontally) {
            ProgressRing(percentage)
            Spacer(Modifier.height(HabitlyTheme.spacing.small))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ProgressRing(percentage: Int) {
    val primary = MaterialTheme.colorScheme.primary
    val track = MaterialTheme.colorScheme.surfaceVariant
    val strokeWidth = HabitlyTheme.spacing.small
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(HabitlyTheme.spacing.generous * 2).semantics { contentDescription = "$percentage percent" },
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            drawArc(track, -90f, 360f, false, style = stroke, size = Size(size.width, size.height), topLeft = Offset.Zero)
            drawArc(primary, -90f, percentage * 3.6f, false, style = stroke, size = Size(size.width, size.height), topLeft = Offset.Zero)
        }
        Text("$percentage%", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun HabitProgressRow(habit: HabitStatsUiState) {
    val spacing = HabitlyTheme.spacing
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.size(spacing.generous).clip(CircleShape).background(Color(android.graphics.Color.parseColor(habit.colorHex)).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) { Text(habit.emoji, style = MaterialTheme.typography.titleLarge) }
        Spacer(Modifier.width(spacing.standard))
        Text(habit.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        LinearProgressIndicator(
            progress = { habit.percentage / 100f },
            modifier = Modifier.weight(1f).semantics { contentDescription = "${habit.percentage} percent" },
            color = Color(android.graphics.Color.parseColor(habit.colorHex)),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(Modifier.width(spacing.small))
        Text("${habit.percentage}%", style = MaterialTheme.typography.labelLarge)
    }
}
