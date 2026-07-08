package com.habitly.app.ui.settings

import android.provider.Settings
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitly.app.BuildConfig
import com.habitly.app.data.ThemeMode
import com.habitly.app.ui.theme.HabitlyTheme

@Composable
fun SettingsRoute(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    SettingsScreen(
        uiState = uiState,
        onThemeSelected = viewModel::setThemeMode,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeSelected: (ThemeMode) -> Unit,
) {
    val spacing = HabitlyTheme.spacing
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(spacing.standard),
        verticalArrangement = Arrangement.spacedBy(spacing.section),
    ) {
        item {
            Text("Appearance", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                ThemeOption("System", ThemeMode.SYSTEM, uiState.themeMode, onThemeSelected)
                ThemeOption("Light", ThemeMode.LIGHT, uiState.themeMode, onThemeSelected)
                ThemeOption("Dark", ThemeMode.DARK, uiState.themeMode, onThemeSelected)
            }
        }
        item {
            Text("Notifications", style = MaterialTheme.typography.titleLarge)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.semantics { contentDescription = "Notifications ${if (uiState.notificationsAllowed) "allowed" else "not allowed"}" },
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = null)
                Spacer(Modifier.width(spacing.small))
                Text(
                    text = if (uiState.notificationsAllowed) "Allowed" else "Not allowed",
                    color = if (uiState.notificationsAllowed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f),
                )
                if (!uiState.notificationsAllowed) {
                    TextButton(onClick = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    }) { Text("Turn on") }
                }
            }
        }
        item {
            Text("About", style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Outlined.Spa,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(spacing.large),
                )
                Spacer(Modifier.width(spacing.small))
                Column {
                    Text("Habitly", style = MaterialTheme.typography.titleMedium)
                    Text("Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text("Runs fully offline. No account, no tracking.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ThemeOption(
    label: String,
    value: ThemeMode,
    selectedThemeMode: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
) {
    FilterChip(
        selected = selectedThemeMode == value,
        onClick = { onThemeSelected(value) },
        label = { Text(label) },
        modifier = Modifier.semantics { contentDescription = "$label theme ${if (selectedThemeMode == value) "selected" else "not selected"}" },
    )
}
