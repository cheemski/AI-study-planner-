package com.example.studyplannerapp.AppNavHost

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studyplannerapp.ui.home.HomeScreen
import com.example.studyplannerapp.ui.notes.NotesScreen
import com.example.studyplannerapp.ui.quiz.QuizScreen
import com.example.studyplannerapp.ui.schedule.ScheduleScreen

/**
 * The main app shell shown after login: a bottom nav bar over a NavHost with
 * the four feature screens.
 */
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(), onLogout: () -> Unit) {
    Scaffold(
        bottomBar = { AppBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.HOME.route) {
                HomeScreen(
                    onNavigate = { destination -> navController.navigate(destination.route) },
                    onLogout = onLogout
                )
            }
            composable(Destination.SCHEDULE.route) { ScheduleScreen() }
            composable(Destination.QUIZ.route) { QuizScreen() }
            composable(Destination.NOTES.route) { NotesScreen() }
        }
    }
}

@Composable
private fun AppBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
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
                label = { Text(destination.label) }
            )
        }
    }
}
