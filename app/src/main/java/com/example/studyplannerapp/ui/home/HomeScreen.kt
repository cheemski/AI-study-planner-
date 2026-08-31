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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyplannerapp.AppNavHost.Destination
import com.example.studyplannerapp.data.Task
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigate: (Destination) -> Unit,
    onLogout: () -> Unit,
    onOpenProfile: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val displayName by viewModel.displayName.collectAsStateWithLifecycle()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }

    ScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
        ) {
            item { HomeHeader(name = displayName, onOpenProfile = onOpenProfile) }
            item { Spacer(Modifier.height(20.dp)) }
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
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = PurpleTint,
                        modifier = Modifier.clickable { showAddTaskDialog = true }
                    ) {
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
            if (tasks.isEmpty()) {
                item {
                    Text(
                        "No tasks yet — tap Add task to create one.",
                        color = InkMuted, fontSize = 14.sp
                    )
                }
            } else {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggle = { viewModel.toggleTask(task.id) },
                        onRemove = { viewModel.removeTask(task.id) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        if (showAddTaskDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddTaskDialog = false
                    newTaskText = ""
                },
                title = { Text("Add task") },
                text = {
                    OutlinedTextField(
                        value = newTaskText,
                        onValueChange = { newTaskText = it },
                        placeholder = { Text("e.g. Revise Chapter 4") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        enabled = newTaskText.isNotBlank(),
                        onClick = {
                            viewModel.addTask(newTaskText)
                            newTaskText = ""
                            showAddTaskDialog = false
                        }
                    ) { Text("Add") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddTaskDialog = false
                        newTaskText = ""
                    }) { Text("Cancel") }
                }
            )
        }
    }
}

// NOTE: LocalDate/DateTimeFormatter need either minSdk 26+ or core library
// desugaring enabled (they're already java.time, not a new dependency) — add
// `coreLibraryDesugaring` in your app module if minSdk is below 26.
@Composable
private fun HomeHeader(name: String, onOpenProfile: () -> Unit) {
    val firstName = name.substringBefore(" ").ifBlank { name }
    val today = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault()))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Hello, Welcome 👋", color = InkMuted, fontSize = 14.sp)
            Text(name, color = InkNavy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("Good Morning,", color = InkNavy, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$firstName! ", color = BrandPurple, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                Text("☀️", fontSize = 26.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text("$today · Ready to learn?", color = InkMuted, fontSize = 14.sp)
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
                Text(firstName.firstOrNull()?.uppercaseChar()?.toString() ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DocumentAiCard(onClick: () -> Unit) {
    GradientHero(brush = HeroBrushes.document, modifier = Modifier.clickable { onClick() }) {
        Column {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.15f)) {
                Icon(
                    Icons.Filled.Description, null,
                    tint = Color.White, modifier = Modifier.padding(8.dp).size(20.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
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
private fun TaskRow(task: Task, onToggle: () -> Unit, onRemove: () -> Unit) {
    val borderColor = if (task.done) AccentGreen else Color.Transparent
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        color = if (task.done) GreenTint else CardWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
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
                modifier = Modifier.weight(1f),
                color = if (task.done) InkMuted else InkNavy,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = if (task.done) TextDecoration.LineThrough else null
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Close, "Remove task", tint = InkMuted)
            }
        }
    }
}