package com.example.studyplannerapp.AppNavHost

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
import com.example.studyplannerapp.ui.profile.ChangePasswordScreen
import com.example.studyplannerapp.ui.profile.EditProfileScreen
import com.example.studyplannerapp.ui.profile.PrivacyPolicyScreen
import com.example.studyplannerapp.ui.profile.ProfileScreen
import com.example.studyplannerapp.ui.quiz.QuizScreen
import com.example.studyplannerapp.ui.schedule.ScheduleScreen

private const val ROUTE_PROFILE = "profile"
private const val ROUTE_EDIT_PROFILE = "edit_profile"
private const val ROUTE_CHANGE_PASSWORD = "change_password"
private const val ROUTE_PRIVACY_POLICY = "privacy_policy"

/**
 * The main app shell shown after login: a bottom nav bar over a NavHost with
 * the four feature screens, plus full-screen routes reached from Profile
 * (no bottom bar on any of these).
 */
// Single navigation helper used by every entry point into a bottom-nav tab
// (the bottom bar itself, and any in-screen shortcut like Home's AI Feature
// cards) so the back stack stays in the same shape no matter how you got
// there. Without this, a plain navController.navigate(route) from inside a
// screen builds a differently-shaped back stack than the bottom bar's
// popUpTo/singleTop/restoreState navigation, and switching tabs afterwards
// can appear to do nothing.
private fun navigateToTab(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(Destination.HOME.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// NOTE: requires the androidx.compose.material3:material3-window-size-class
// dependency, and androidx.activity:activity-compose 1.9.0+ for LocalActivity
// (add/bump these if they're not already on the classpath). We key off
// window *width* rather than raw orientation so a large-enough portrait
// window (e.g. a tablet, or a resizable/split-screen window) also gets the
// rail, and a small landscape window (e.g. a phone in a narrow split) still
// gets the bottom bar. If your installed version's calculateWindowSizeClass
// signature differs, adjust just this one call.
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(), onLogout: () -> Unit) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = Destination.entries.any { it.route == currentRoute }

    val activity = LocalActivity.current
    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }
    val useRail = windowSizeClass != null && windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    Row(modifier = Modifier.fillMaxSize()) {
        if (showBottomBar && useRail) {
            AppNavigationRail(navController, currentRoute)
        }
        Scaffold(
            modifier = Modifier.weight(1f),
            bottomBar = { if (showBottomBar && !useRail) AppBottomBar(navController, currentRoute) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destination.HOME.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Destination.HOME.route) {
                    HomeScreen(
                        onNavigate = { destination -> navigateToTab(navController, destination.route) },
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
                        onLogout = onLogout,
                        onEditProfile = { navController.navigate(ROUTE_EDIT_PROFILE) },
                        onChangePassword = { navController.navigate(ROUTE_CHANGE_PASSWORD) },
                        onPrivacyPolicy = { navController.navigate(ROUTE_PRIVACY_POLICY) }
                    )
                }
                composable(ROUTE_EDIT_PROFILE) {
                    EditProfileScreen(onBack = { navController.popBackStack() })
                }
                composable(ROUTE_CHANGE_PASSWORD) {
                    ChangePasswordScreen(onBack = { navController.popBackStack() })
                }
                composable(ROUTE_PRIVACY_POLICY) {
                    PrivacyPolicyScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

// Same floating-card treatment as the bottom bar, just vertical — shown
// instead of AppBottomBar when the device is in landscape.
@Composable
private fun AppNavigationRail(navController: NavHostController, currentRoute: String?) {
    Surface(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp
    ) {
        NavigationRail(
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Destination.entries.forEach { destination ->
                NavigationRailItem(
                    selected = currentRoute == destination.route,
                    onClick = { navigateToTab(navController, destination.route) },
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    colors = NavigationRailItemDefaults.colors(
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
                    onClick = { navigateToTab(navController, destination.route) },
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