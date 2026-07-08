package com.habitly.app.ui.onboarding

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.habitly.app.HabitlyApplication
import com.habitly.app.ui.theme.HabitlyTheme
import kotlinx.coroutines.launch

@Composable
fun OnboardingRoute(onFinished: () -> Unit) {
    val app = LocalContext.current.applicationContext as HabitlyApplication
    val scope = rememberCoroutineScope()
    OnboardingScreen(
        onGetStarted = {
            scope.launch {
                app.preferencesRepository.setHasCompletedOnboarding(true)
                onFinished()
            }
        },
    )
}

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val spacing = HabitlyTheme.spacing
    Column(
        modifier = Modifier.fillMaxSize().padding(spacing.standard),
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
            Box(Modifier.size(spacing.small).background(MaterialTheme.colorScheme.primary, CircleShape))
            Box(Modifier.size(spacing.small).background(MaterialTheme.colorScheme.outlineVariant, CircleShape))
        }
        Spacer(Modifier.height(spacing.large))
        Button(onClick = onGetStarted, modifier = Modifier.fillMaxWidth(), shape = CircleShape) {
            Text("Get Started")
        }
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
