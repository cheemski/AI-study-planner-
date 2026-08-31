package com.example.studyplannerapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dynamic color (Material You) is intentionally NOT used here — this app has
// its own indigo/lavender identity that shouldn't shift with wallpaper.
//
// secondaryContainer / onSecondaryContainer drive Material3's default
// NavigationBar selected-pill look, so setting those to the sampled lavender
// + indigo automatically reproduces the Home/Document/Planner/Quiz nav asset
// without custom bottom-bar styling code.

private val DarkColorScheme = darkColorScheme(
    primary = IndigoAccent,
    onPrimary = Color.White,
    secondary = IndigoAccent,
    secondaryContainer = LavenderContainerDark,
    onSecondaryContainer = LavenderContainer,
    tertiary = Pink80,
    background = IndigoDark,
    onBackground = LavenderContainer,
    surface = Color(0xFF1E1836),
    onSurface = LavenderContainer
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoAccent,
    onPrimary = Color.White,
    secondary = IndigoAccent,
    secondaryContainer = LavenderContainer,
    onSecondaryContainer = IndigoAccent,
    tertiary = Pink40,
    background = SurfaceOffWhite,
    onBackground = InkIndigo,
    surface = SurfaceWhite,
    onSurface = InkIndigo
)

@Composable
fun StudyPlannerAppTheme(
    // Forced to light for now: ScreenBackground/CardWhite/InkNavy etc. across
    // the app are hardcoded light-mode colors rather than reading from
    // MaterialTheme.colorScheme, so letting this follow isSystemInDarkTheme()
    // desyncs unstyled components (e.g. a default OutlinedTextField's text
    // color) from those hardcoded backgrounds — invisible text on a device
    // with system dark mode on, even though nothing else in the UI actually
    // looks dark. Re-enable `isSystemInDarkTheme()` once the rest of the UI
    // is wired to the theme instead of hardcoded colors.
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}