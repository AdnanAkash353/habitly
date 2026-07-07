package com.habitly.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.habitly.app.data.HeatmapDay
import com.habitly.app.data.SampleData
import com.habitly.app.data.SampleHabit
import com.habitly.app.ui.theme.HabitlyTheme
import java.time.format.DateTimeFormatter

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val spacing = HabitlyTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.standard),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SproutIllustration(Modifier.size(spacing.generous * 3))
        Spacer(Modifier.height(spacing.generous))
        Text("Small steps, every day", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Text(
            "Habitly helps you build routines that stick — no accounts, no tracking, just you and your habits.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(spacing.section))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
            Box(Modifier.size(spacing.small).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
            Box(Modifier.size(spacing.small).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant))
        }
        Spacer(Modifier.height(spacing.large))
        Button(onClick = onGetStarted, modifier = Modifier.fillMaxWidth(), shape = CircleShape) {
            Text("Get Started")
        }
    }
}

@Composable
fun HomeScreen(onAddHabit: () -> Unit, onOpenHabit: (Int) -> Unit) {
    val spacing = HabitlyTheme.spacing
    var completedIds by remember { mutableStateOf(SampleData.completedTodayHabitIds) }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(spacing.standard),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        item {
            Text("Today", style = MaterialTheme.typography.headlineSmall)
            Text("Keep growing one small habit at a time.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(spacing.compact))
        }
        items(SampleData.habits) { habit ->
            val isComplete = habit.id in completedIds
            HabitCard(
                habit = habit,
                isComplete = isComplete,
                onToggle = {
                    completedIds = if (isComplete) completedIds - habit.id else completedIds + habit.id
                },
                onOpen = { onOpenHabit(habit.id) },
            )
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
fun AddEditHabitScreen(isEdit: Boolean) {
    val spacing = HabitlyTheme.spacing
    var name by remember { mutableStateOf(if (isEdit) SampleData.habits.first().name else "") }
    var specificDays by remember { mutableStateOf(isEdit) }
    var reminder by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(spacing.standard),
        verticalArrangement = Arrangement.spacedBy(spacing.section),
    ) {
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 40) name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") },
                supportingText = { Text("${name.length}/40") },
                singleLine = true,
            )
        }
        item { EmojiPickerPreview() }
        item { ColorPickerPreview() }
        item {
            Text("Frequency", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                FilterChip(selected = !specificDays, onClick = { specificDays = false }, label = { Text("Every day") })
                FilterChip(selected = specificDays, onClick = { specificDays = true }, label = { Text("Specific days") })
            }
            if (specificDays) WeekdayChipsPreview()
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("Reminder", style = MaterialTheme.typography.titleMedium)
                    Text(if (reminder) "Time 8:00 AM" else "Off", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = reminder, onCheckedChange = { reminder = it })
            }
        }
        item {
            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = CircleShape) { Text("Save") }
            if (isEdit) {
                TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Text("Delete Habit", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun HabitDetailScreen(onEdit: () -> Unit) {
    val habit = SampleData.habits.first()
    val spacing = HabitlyTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(spacing.standard),
        verticalArrangement = Arrangement.spacedBy(spacing.section),
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                EmojiChip(habit)
                Text(habit.name, style = MaterialTheme.typography.headlineSmall)
                TextButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = null)
                    Text("Edit")
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                StreakStat("Current streak", habit.currentStreak, Modifier.weight(1f))
                StreakStat("Best streak", habit.bestStreak, Modifier.weight(1f))
            }
        }
        item { HeatmapCard(SampleData.heatmapDays()) }
    }
}

@Composable
fun StatsScreen() {
    val spacing = HabitlyTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(spacing.standard),
        verticalArrangement = Arrangement.spacedBy(spacing.section),
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                ProgressCard("This week", 76, Modifier.weight(1f))
                ProgressCard("This month", 69, Modifier.weight(1f))
            }
        }
        item { Text("By Habit", style = MaterialTheme.typography.titleLarge) }
        items(SampleData.habits) { habit -> HabitProgressRow(habit) }
    }
}

@Composable
fun SettingsScreen() {
    val spacing = HabitlyTheme.spacing
    var selectedTheme by remember { mutableStateOf("System") }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(spacing.standard),
        verticalArrangement = Arrangement.spacedBy(spacing.section),
    ) {
        item {
            Text("Appearance", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                listOf("System", "Light", "Dark").forEach { option ->
                    FilterChip(selected = selectedTheme == option, onClick = { selectedTheme = option }, label = { Text(option) })
                }
            }
        }
        item {
            Text("Notifications", style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Notifications, contentDescription = null)
                Spacer(Modifier.width(spacing.small))
                Text("Not allowed", color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
                TextButton(onClick = {}) { Text("Turn on") }
            }
        }
        item {
            Text("About", style = MaterialTheme.typography.titleLarge)
            Text("Habitly", style = MaterialTheme.typography.titleMedium)
            Text("Runs fully offline. No account, no tracking.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun HabitCard(habit: SampleHabit, isComplete: Boolean, onToggle: () -> Unit, onOpen: () -> Unit) {
    val spacing = HabitlyTheme.spacing
    Card(
        onClick = onOpen,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = HabitlyTheme.elevation.level1),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.alpha(if (isComplete) 0.6f else 1f),
    ) {
        Row(Modifier.fillMaxWidth().padding(spacing.standard), verticalAlignment = Alignment.CenterVertically) {
            EmojiChip(habit)
            Spacer(Modifier.width(spacing.standard))
            Column(Modifier.weight(1f)) {
                Text(habit.name, style = MaterialTheme.typography.titleMedium)
                Text("🍃 ${habit.currentStreak} day streak", style = MaterialTheme.typography.labelMedium, color = HabitlyTheme.colors.streakAccent)
            }
            Box(
                Modifier
                    .size(spacing.generous)
                    .clip(CircleShape)
                    .background(if (isComplete) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .border(spacing.micro / 2, MaterialTheme.colorScheme.outline, CircleShape)
                    .clickable(onClick = onToggle)
                    .semantics { contentDescription = if (isComplete) "Mark ${habit.name} incomplete" else "Mark ${habit.name} complete" },
                contentAlignment = Alignment.Center,
            ) {
                if (isComplete) Icon(Icons.Outlined.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun EmojiChip(habit: SampleHabit) {
    val color = Color(android.graphics.Color.parseColor(habit.colorHex))
    Box(
        modifier = Modifier.size(HabitlyTheme.spacing.generous).clip(CircleShape).background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) { Text(habit.emoji, style = MaterialTheme.typography.titleLarge) }
}

@Composable
private fun EmojiPickerPreview() {
    val emojis = listOf("🌱", "💧", "📚", "🏃", "🧘", "😴", "🍎", "💪", "🎨", "🎯")
    Text("Icon", style = MaterialTheme.typography.titleMedium)
    Row(horizontalArrangement = Arrangement.spacedBy(HabitlyTheme.spacing.small)) {
        emojis.take(5).forEach { Text(it, style = MaterialTheme.typography.headlineSmall) }
    }
}

@Composable
private fun ColorPickerPreview() {
    val colors = listOf("#2F6F5E", "#E8A33D", "#6B8E4E", "#4A7FA6", "#C4653A", "#8E4A6B", "#5B6470", "#5C5EA6")
    Text("Color", style = MaterialTheme.typography.titleMedium)
    Row(horizontalArrangement = Arrangement.spacedBy(HabitlyTheme.spacing.small)) {
        colors.forEachIndexed { index, hex ->
            Box(
                Modifier
                    .size(HabitlyTheme.spacing.large)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(hex)))
                    .border(if (index == 0) HabitlyTheme.spacing.micro / 2 else HabitlyTheme.elevation.level0, MaterialTheme.colorScheme.onSurface, CircleShape),
            )
        }
    }
}

@Composable
private fun WeekdayChipsPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(HabitlyTheme.spacing.small)) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEachIndexed { index, day ->
            FilterChip(selected = index % 2 == 0, onClick = {}, label = { Text(day) })
        }
    }
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
private fun HeatmapCard(days: List<HeatmapDay>) {
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
        }
    }
}

@Composable
private fun HeatmapCell(day: HeatmapDay) {
    val formatter = DateTimeFormatter.ofPattern("MMMM d")
    Box(
        Modifier
            .size(HabitlyTheme.spacing.compact)
            .background(if (day.completed) HabitlyTheme.colors.heatmapFill else Color.Transparent, MaterialTheme.shapes.extraSmall)
            .border(HabitlyTheme.spacing.micro / 4, MaterialTheme.colorScheme.outline.copy(alpha = if (day.future) 0.38f else 1f), MaterialTheme.shapes.extraSmall)
            .semantics { contentDescription = "${day.date.format(formatter)}, ${if (day.completed) "completed" else "not completed"}" },
    )
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
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(HabitlyTheme.spacing.generous * 2).semantics { contentDescription = "$percentage percent" }) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            drawArc(track, -90f, 360f, false, style = stroke, size = Size(size.width, size.height), topLeft = Offset.Zero)
            drawArc(primary, -90f, percentage * 3.6f, false, style = stroke, size = Size(size.width, size.height), topLeft = Offset.Zero)
        }
        Text("$percentage%", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun HabitProgressRow(habit: SampleHabit) {
    val spacing = HabitlyTheme.spacing
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        EmojiChip(habit)
        Spacer(Modifier.width(spacing.standard))
        Text(habit.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        LinearProgressIndicator(
            progress = { habit.completionRate / 100f },
            modifier = Modifier.weight(1f).semantics { contentDescription = "${habit.completionRate} percent" },
            color = Color(android.graphics.Color.parseColor(habit.colorHex)),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(Modifier.width(spacing.small))
        Text("${habit.completionRate}%", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun SproutIllustration(modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val container = MaterialTheme.colorScheme.primaryContainer
    val stemWidth = HabitlyTheme.spacing.compact
    Canvas(modifier) {
        drawCircle(container)
        drawLine(primary, Offset(size.width / 2, size.height * 0.75f), Offset(size.width / 2, size.height * 0.35f), strokeWidth = stemWidth.toPx(), cap = StrokeCap.Round)
        drawOval(primary, topLeft = Offset(size.width * 0.18f, size.height * 0.25f), size = Size(size.width * 0.34f, size.height * 0.24f))
        drawOval(primary.copy(alpha = 0.75f), topLeft = Offset(size.width * 0.48f, size.height * 0.2f), size = Size(size.width * 0.36f, size.height * 0.25f))
    }
}
