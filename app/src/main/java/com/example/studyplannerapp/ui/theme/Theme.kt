package com.example.studyplannerapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
