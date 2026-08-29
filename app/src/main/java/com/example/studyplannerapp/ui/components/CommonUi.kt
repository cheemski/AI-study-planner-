package com.example.studyplannerapp.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyplannerapp.ui.theme.CardWhite
import com.example.studyplannerapp.ui.theme.InkMuted
import com.example.studyplannerapp.ui.theme.InkNavy
import com.example.studyplannerapp.ui.theme.ScreenLavender

// Full-screen lavender background wrapper.
@Composable
fun ScreenBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenLavender)
    ) {
        content()
    }
}

// A big rounded gradient hero card (used on Document / Planner / Quiz screens).
@Composable
fun GradientHero(
    brush: Brush,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(brush)
            .padding(22.dp)
    ) {
        content()
    }
}

// White stat card with a big colored number over a caption.
@Composable
fun StatCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
    containerColor: Color = CardWhite
) {
    Surface(
        modifier = modifier.height(96.dp),
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
            Spacer(Modifier.height(2.dp))
            Text(label, fontSize = 13.sp, color = InkMuted)
        }
    }
}

// Section title, optionally with a trailing action composable on the right.
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = InkNavy)
        trailing?.invoke()
    }
}

// Small rounded top-left back button used on subpages.
@Composable
fun CircleBackButton(onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = CardWhite,
        shadowElevation = 2.dp,
        modifier = Modifier.size(44.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = com.example.studyplannerapp.ui.theme.BrandPurple
            )
        }
    }
}

// Gradient brush helpers matching the design's hero cards.
object HeroBrushes {
    val document: Brush
        get() = Brush.linearGradient(
            listOf(
                com.example.studyplannerapp.ui.theme.DocGradStart,
                com.example.studyplannerapp.ui.theme.DocGradEnd
            )
        )
    val planner: Brush
        get() = Brush.linearGradient(
            listOf(
                com.example.studyplannerapp.ui.theme.PlannerGradStart,
                com.example.studyplannerapp.ui.theme.PlannerGradEnd
            )
        )
    val quiz: Brush
        get() = Brush.linearGradient(
            listOf(
                com.example.studyplannerapp.ui.theme.QuizGradStart,
                com.example.studyplannerapp.ui.theme.QuizGradEnd
            )
        )
}
