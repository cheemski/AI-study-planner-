package com.example.studyplannerapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dynamic color (Material You) is intentionally NOT used here — this app has
// its own warm "reading nook" identity that shouldn't shift with wallpaper.

private val DarkColorScheme = darkColorScheme(
    primary = WarmTan,
    onPrimary = InkBrown,
    secondary = AccentGold,
    tertiary = Pink80,
    background = ParchmentDark,
    onBackground = InkCream,
    surface = CreamCardDark,
    onSurface = InkCream
)

private val LightColorScheme = lightColorScheme(
    primary = WalnutBrown,
    onPrimary = Color.White,
    secondary = AccentGold,
    tertiary = Pink40,
    background = Parchment,
    onBackground = InkBrown,
    surface = CreamCard,
    onSurface = InkBrown
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
