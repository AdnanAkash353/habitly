package com.habitly.app.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.habitly.app.ui.theme.HabitlyTheme
import com.habitly.app.ui.addedit.AddEditHabitRoute
import com.habitly.app.ui.detail.DetailRoute
import com.habitly.app.ui.home.HomeRoute
import com.habitly.app.ui.onboarding.OnboardingRoute
import com.habitly.app.ui.settings.SettingsRoute
import com.habitly.app.ui.stats.StatsRoute
import com.habitly.app.ui.screens.AddEditHabitScreen
import com.habitly.app.ui.screens.HabitDetailScreen
import com.habitly.app.ui.screens.HomeScreen
import com.habitly.app.ui.screens.OnboardingScreen
import com.habitly.app.ui.screens.SettingsScreen
import com.habitly.app.ui.screens.StatsScreen

sealed class HabitlyRoute(val route: String, val title: String) {
    data object Onboarding : HabitlyRoute("onboarding", "Habitly")
    data object Home : HabitlyRoute("home", "Home")
    data object Stats : HabitlyRoute("stats", "Stats")
    data object Settings : HabitlyRoute("settings", "Settings")
    data object AddHabit : HabitlyRoute("add_edit", "Add Habit")
    data object EditHabit : HabitlyRoute("add_edit/{habitId}", "Edit Habit") {
        fun createRoute(habitId: Int) = "add_edit/$habitId"
    }
    data object HabitDetail : HabitlyRoute("habit_detail/{habitId}", "Habit Detail") {
        fun createRoute(habitId: Int) = "habit_detail/$habitId"
    }
}

private val bottomDestinations = listOf(
    HabitlyRoute.Home,
    HabitlyRoute.Stats,
    HabitlyRoute.Settings,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitlyNavGraph(
    showOnboarding: Boolean,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route ?: HabitlyRoute.Home.route
    val currentHabitId = backStackEntry?.arguments?.getInt("habitId")
    val showBottomBar = currentRoute in bottomDestinations.map { it.route }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (currentRoute != HabitlyRoute.Onboarding.route) {
                HabitlyTopBar(
                    currentRoute = currentRoute,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateAdd = { navController.navigate(HabitlyRoute.AddHabit.route) },
                    onNavigateEdit = { currentHabitId?.let { navController.navigate(HabitlyRoute.EditHabit.createRoute(it)) } },
                )
            }
            HabitlyTopBar(
                currentRoute = currentRoute,
                onNavigateBack = { navController.popBackStack() },
                onNavigateAdd = { navController.navigate(HabitlyRoute.AddHabit.route) },
                onNavigateEdit = { navController.navigate(HabitlyRoute.EditHabit.createRoute(1)) },
                onNavigateDetail = { navController.navigate(HabitlyRoute.HabitDetail.createRoute(1)) },
            )
        },
        bottomBar = {
            if (showBottomBar) {
                HabitlyBottomBar(
                    currentDestination = currentDestination,
                    onDestinationSelected = { route ->
                        navController.navigate(route.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (showOnboarding) HabitlyRoute.Onboarding.route else HabitlyRoute.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(HabitlyRoute.Onboarding.route) {
                OnboardingRoute(onFinished = {
            startDestination = HabitlyRoute.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(HabitlyRoute.Onboarding.route) {
                OnboardingScreen(onGetStarted = {
                    navController.navigate(HabitlyRoute.Home.route) {
                        popUpTo(HabitlyRoute.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(HabitlyRoute.Home.route) {
                HomeRoute(
                HomeScreen(
                    onAddHabit = { navController.navigate(HabitlyRoute.AddHabit.route) },
                    onOpenHabit = { habitId -> navController.navigate(HabitlyRoute.HabitDetail.createRoute(habitId)) },
                )
            }
            composable(HabitlyRoute.Stats.route) {
                StatsRoute()
            }
            composable(HabitlyRoute.Settings.route) {
                SettingsRoute()
            }
            composable(HabitlyRoute.AddHabit.route) {
                AddEditHabitRoute(habitId = null, onDone = { navController.popBackStack() })
                StatsScreen()
            }
            composable(HabitlyRoute.Settings.route) {
                SettingsScreen()
            }
            composable(HabitlyRoute.AddHabit.route) {
                AddEditHabitScreen(isEdit = false)
            }
            composable(
                route = HabitlyRoute.EditHabit.route,
                arguments = listOf(navArgument("habitId") { type = NavType.IntType }),
            ) { backStackEntry ->
                AddEditHabitRoute(
                    habitId = backStackEntry.arguments?.getInt("habitId"),
                    onDone = { navController.popBackStack() },
                )
            ) {
                AddEditHabitScreen(isEdit = true)
            }
            composable(
                route = HabitlyRoute.HabitDetail.route,
                arguments = listOf(navArgument("habitId") { type = NavType.IntType }),
            ) { backStackEntry ->
                backStackEntry.arguments?.getInt("habitId")?.let { habitId ->
                    DetailRoute(
                        habitId = habitId,
                        onEdit = { id -> navController.navigate(HabitlyRoute.EditHabit.createRoute(id)) },
                        onDeleted = { navController.popBackStack() },
                    )
                }
            ) {
                HabitDetailScreen(onEdit = { navController.navigate(HabitlyRoute.EditHabit.createRoute(1)) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitlyTopBar(
    currentRoute: String,
    onNavigateBack: () -> Unit,
    onNavigateAdd: () -> Unit,
    onNavigateEdit: () -> Unit,
    onNavigateDetail: () -> Unit,
) {
    val title = when (currentRoute) {
        HabitlyRoute.Onboarding.route -> HabitlyRoute.Onboarding.title
        HabitlyRoute.Home.route -> HabitlyRoute.Home.title
        HabitlyRoute.Stats.route -> HabitlyRoute.Stats.title
        HabitlyRoute.Settings.route -> HabitlyRoute.Settings.title
        HabitlyRoute.AddHabit.route -> HabitlyRoute.AddHabit.title
        HabitlyRoute.EditHabit.route -> HabitlyRoute.EditHabit.title
        HabitlyRoute.HabitDetail.route -> HabitlyRoute.HabitDetail.title
        else -> HabitlyRoute.Home.title
    }

    val isPushedRoute = currentRoute == HabitlyRoute.AddHabit.route ||
        currentRoute == HabitlyRoute.EditHabit.route ||
        currentRoute == HabitlyRoute.HabitDetail.route

    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.headlineSmall) },
        navigationIcon = {
            if (isPushedRoute) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Navigate back")
                }
            }
        },
        actions = {
            if (currentRoute == HabitlyRoute.HabitDetail.route) {
                IconButton(onClick = onNavigateEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit habit")
                }
            }
            if (currentRoute == HabitlyRoute.Home.route) {
                IconButton(onClick = onNavigateDetail) {
                    Icon(Icons.Outlined.Info, contentDescription = "Open habit detail")
                }
                IconButton(onClick = onNavigateAdd) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add Habit")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
    )
}

@Composable
private fun HabitlyBottomBar(
    currentDestination: NavDestination?,
    onDestinationSelected: (HabitlyRoute) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = HabitlyTheme.elevation.level2,
    ) {
        bottomDestinations.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination?.route == destination.route,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = when (destination) {
                            HabitlyRoute.Home -> Icons.Outlined.Home
                            HabitlyRoute.Stats -> Icons.Outlined.BarChart
                            HabitlyRoute.Settings -> Icons.Outlined.Settings
                            else -> Icons.Outlined.Home
                        },
                        contentDescription = destination.title,
                    )
                },
                label = { Text(destination.title, style = MaterialTheme.typography.labelLarge) },
            )
        }
    }
}
