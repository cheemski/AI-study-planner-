package com.example.studyplannerapp.AppNavHost

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.ui.graphics.vector.ImageVector

// Bottom-nav destinations, in the order they appear in the tab bar.
enum class Destination(val route: String, val label: String, val icon: ImageVector) {
    HOME("home", "Home", Icons.Filled.Home),
    SCHEDULE("schedule", "Schedule", Icons.Filled.CalendarMonth),
    QUIZ("quiz", "Quiz", Icons.Filled.Quiz),
    NOTES("notes", "Notes", Icons.Filled.Description)
}
