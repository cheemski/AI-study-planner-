package com.example.studyplannerapp.AppNavHost

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studyplannerapp.ui.home.HomeScreen
import com.example.studyplannerapp.ui.notes.NotesScreen
import com.example.studyplannerapp.ui.profile.ProfileScreen
import com.example.studyplannerapp.ui.quiz.QuizScreen
import com.example.studyplannerapp.ui.schedule.ScheduleScreen

private const val ROUTE_PROFILE = "profile"

/**
 * The main app shell shown after login: a bottom nav bar over a NavHost with
 * the four feature screens, plus a full-screen Profile route (no bottom bar).
 */
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(), onLogout: () -> Unit) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = Destination.entries.any { it.route == currentRoute }

    Scaffold(
        bottomBar = { if (showBottomBar) AppBottomBar(navController, currentRoute) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.HOME.route) {
                HomeScreen(
                    onNavigate = { destination -> navController.navigate(destination.route) },
                    onLogout = onLogout,
                    onOpenProfile = { navController.navigate(ROUTE_PROFILE) }
                )
            }
            composable(Destination.PLANNER.route) { ScheduleScreen() }
            composable(Destination.QUIZ.route) { QuizScreen() }
            composable(Destination.DOCUMENT.route) { NotesScreen() }
            composable(ROUTE_PROFILE) {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = onLogout
                )
            }
        }
    }
}

// Floating rounded pill bar, matching the nav asset (white card, inset from
// the screen edges, indigo icon/label on a lavender pill when selected).
@Composable
private fun AppBottomBar(navController: NavHostController, currentRoute: String?) {
    Surface(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            Destination.entries.forEach { destination ->
                NavigationBarItem(
                    selected = currentRoute == destination.route,
                    onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(Destination.HOME.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}
