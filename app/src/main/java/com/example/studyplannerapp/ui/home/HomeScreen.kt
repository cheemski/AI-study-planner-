package com.example.studyplannerapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyplannerapp.AppNavHost.Destination
import com.example.studyplannerapp.ui.components.GradientHero
import com.example.studyplannerapp.ui.components.HeroBrushes
import com.example.studyplannerapp.ui.components.ScreenBackground
import com.example.studyplannerapp.ui.components.SectionHeader
import com.example.studyplannerapp.ui.components.StatCard
import com.example.studyplannerapp.ui.theme.AccentGreen
import com.example.studyplannerapp.ui.theme.BlueTint
import com.example.studyplannerapp.ui.theme.BrandPurple
import com.example.studyplannerapp.ui.theme.CardWhite
import com.example.studyplannerapp.ui.theme.GreenTint
import com.example.studyplannerapp.ui.theme.InkMuted
import com.example.studyplannerapp.ui.theme.InkNavy
import com.example.studyplannerapp.ui.theme.PinkTint
import com.example.studyplannerapp.ui.theme.PurpleTint

private data class Task(val title: String, val done: Boolean)

@Composable
fun HomeScreen(
    onNavigate: (Destination) -> Unit,
    onLogout: () -> Unit,
    onOpenProfile: () -> Unit = {}
) {
    val tasks = remember {
        mutableStateListOf(
            Task("Revise Chapter 4 – Algorithm", false),
            Task("Submit Marketing Essay", true),
            Task("Practise Calculus Problem", false)
        )
    }

    ScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
        ) {
            item { HomeHeader(onOpenProfile = onOpenProfile) }
            item { Spacer(Modifier.height(20.dp)) }
            item { SearchBar() }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatCardWithBar("Study Time", "3h 20m", "This week", BrandPurple, 0.4f, Modifier.weight(1f))
                    StatCardWithBar("Tasks Done", "1/3", null, AccentGreen, 0.33f, Modifier.weight(1f))
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
            item { SectionHeader("AI Features") }
            item { Spacer(Modifier.height(12.dp)) }
            item { DocumentAiCard(onClick = { onNavigate(Destination.DOCUMENT) }) }
            item { Spacer(Modifier.height(14.dp)) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    SmallFeatureCard(
                        "AI Planner", "Career-driven course picks", "Explore >",
                        BlueTint, Color(0xFF2F6FE0), Modifier.weight(1f)
                    ) { onNavigate(Destination.PLANNER) }
                    SmallFeatureCard(
                        "AI Quiz", "Auto-generated practise tests", "Practice >",
                        PinkTint, Color(0xFFE0567B), Modifier.weight(1f)
                    ) { onNavigate(Destination.QUIZ) }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
            item {
                SectionHeader("Today's Plan", trailing = {
                    Surface(shape = RoundedCornerShape(50), color = PurpleTint) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Add, null, tint = BrandPurple, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(4.dp))
                            Text("Add task", color = BrandPurple, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                })
            }
            item { Spacer(Modifier.height(12.dp)) }
            itemsIndexed(tasks) { index, task ->
                TaskRow(task) {
                    tasks[index] = task.copy(done = !task.done)
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun HomeHeader(onOpenProfile: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Hello, Welcome 👋", color = InkMuted, fontSize = 14.sp)
            Text("Alex Tan", color = InkNavy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("Good Morning,", color = InkNavy, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Alex! ", color = BrandPurple, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                Text("☀️", fontSize = 26.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text("Thursday, 3 July 2026 · Ready to learn?", color = InkMuted, fontSize = 14.sp)
        }
    }
    Spacer(Modifier.height(4.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Notifications, "Notifications", tint = InkNavy)
        Spacer(Modifier.size(14.dp))
        Surface(
            shape = RoundedCornerShape(50),
            color = BrandPurple,
            modifier = Modifier.size(40.dp).clickable { onOpenProfile() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("A", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SearchBar() {
    Surface(shape = RoundedCornerShape(16.dp), color = CardWhite, shadowElevation = 1.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Search, null, tint = InkMuted)
            Spacer(Modifier.size(10.dp))
            Text("Search documents, courses", color = InkMuted, fontSize = 15.sp)
        }
    }
}

@Composable
private fun StatCardWithBar(
    label: String, value: String, sub: String?, accent: Color, progress: Float, modifier: Modifier
) {
    Surface(modifier = modifier.height(140.dp), shape = RoundedCornerShape(18.dp), color = CardWhite, shadowElevation = 1.dp) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = InkMuted, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))
            Text(value, color = InkNavy, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            if (sub != null) {
                Spacer(Modifier.height(4.dp))
                Text(sub, color = InkMuted, fontSize = 12.sp)
            }
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50))
                    .background(accent.copy(alpha = 0.15f))
            ) {
                Box(
                    Modifier.fillMaxWidth(progress).height(6.dp).clip(RoundedCornerShape(50)).background(accent)
                )
            }
        }
    }
}

@Composable
private fun DocumentAiCard(onClick: () -> Unit) {
    GradientHero(brush = HeroBrushes.document, modifier = Modifier.height(200.dp).clickable { onClick() }) {
        Column {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.15f)) {
                Icon(
                    Icons.Filled.Description, null,
                    tint = Color.White, modifier = Modifier.padding(8.dp).size(20.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            Text("Document AI", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Upload notes · AI summarises & organises", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.2f)) {
                Text("Get started ↗", color = Color.White, fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            }
        }
    }
}

@Composable
private fun SmallFeatureCard(
    title: String, subtitle: String, action: String,
    bg: Color, accent: Color, modifier: Modifier, onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(170.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = bg
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = InkNavy, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(subtitle, color = InkNavy.copy(alpha = 0.65f), fontSize = 13.sp)
            Spacer(Modifier.weight(1f))
            Text(action, color = accent, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun TaskRow(task: Task, onToggle: () -> Unit) {
    val borderColor = if (task.done) AccentGreen else Color.Transparent
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        color = if (task.done) GreenTint else CardWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (task.done) {
                Icon(Icons.Filled.CheckCircle, null, tint = AccentGreen)
            } else {
                Icon(Icons.Outlined.Circle, null, tint = InkMuted.copy(alpha = 0.4f))
            }
            Spacer(Modifier.size(14.dp))
            Text(
                task.title,
                color = if (task.done) InkMuted else InkNavy,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = if (task.done) TextDecoration.LineThrough else null
            )
        }
    }
}
