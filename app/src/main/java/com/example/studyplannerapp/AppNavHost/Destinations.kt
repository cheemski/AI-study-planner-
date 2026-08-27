package com.example.studyplannerapp.AppNavHost

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.ui.graphics.vector.ImageVector

// Bottom-nav destinations, in the order they appear in the tab bar.
// Order/labels/icon shapes match the Home / Document / Planner / Quiz nav
// asset from the design (house, file, calendar, open book).
enum class Destination(val route: String, val label: String, val icon: ImageVector) {
    HOME("home", "Home", Icons.Outlined.Home),
    DOCUMENT("notes", "Document", Icons.Outlined.Description),
    PLANNER("schedule", "Planner", Icons.Outlined.CalendarMonth),
    QUIZ("quiz", "Quiz", Icons.Outlined.MenuBook)
}
