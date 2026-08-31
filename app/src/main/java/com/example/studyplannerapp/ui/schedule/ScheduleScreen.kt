package com.example.studyplannerapp.ui.schedule

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyplannerapp.ui.components.GradientHero
import com.example.studyplannerapp.ui.components.HeroBrushes
import com.example.studyplannerapp.ui.components.ScreenBackground
import com.example.studyplannerapp.ui.components.SectionHeader
import com.example.studyplannerapp.ui.theme.AccentGreen
import com.example.studyplannerapp.ui.theme.AccentOrange
import com.example.studyplannerapp.ui.theme.BrandPurple
import com.example.studyplannerapp.ui.theme.CardWhite
import com.example.studyplannerapp.ui.theme.GreenTint
import com.example.studyplannerapp.ui.theme.InkMuted
import com.example.studyplannerapp.ui.theme.InkNavy
import com.example.studyplannerapp.ui.theme.OrangeTint
import com.example.studyplannerapp.ui.theme.PlannerGradStart
import com.example.studyplannerapp.ui.theme.PurpleTint

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val subjectCount = uiState.subjectsText.split(",").map { it.trim() }.filter { it.isNotBlank() }.size
    val freeDayCount = uiState.dayInputs.count { it.isFree }

    ScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
        ) {
            item {
                Column {
                    Text("Career-driven course", color = InkMuted, fontSize = 14.sp)
                    Text("AI Planner", color = InkNavy, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
            item { PlannerHero() }
            item { Spacer(Modifier.height(16.dp)) }

            item { Spacer(Modifier.height(24.dp)) }

            item { SectionHeader("Build your week") }
            item { Spacer(Modifier.height(12.dp)) }
            item {
                OutlinedTextField(
                    value = uiState.subjectsText,
                    onValueChange = viewModel::onSubjectsChange,
                    label = { Text("Subjects (comma separated)") },
                    placeholder = { Text("Programming, Biology") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = InkNavy,
                        unfocusedTextColor = InkNavy
                    )
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            items(uiState.dayInputs.size) { i ->
                val dayInput = uiState.dayInputs[i]
                DayRow(
                    dayInput = dayInput,
                    onToggle = { viewModel.onDayToggle(dayInput.day, it) },
                    onStart = { viewModel.onStartTimeChange(dayInput.day, it) },
                    onEnd = { viewModel.onEndTimeChange(dayInput.day, it) }
                )
            }
            item {
                uiState.errorMessage?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = com.example.studyplannerapp.ui.theme.AccentRed, fontSize = 14.sp)
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = viewModel::generatePlan,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text("Generate my schedule", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            uiState.plan?.let { plan ->
                item {
                    SectionHeader("This Week 📋")
                    Spacer(Modifier.height(6.dp))
                    Text(plan.weekOverview, color = InkMuted, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                }
                items(plan.days.size) { i ->
                    val day = plan.days[i]
                    if (day.sessions.isNotEmpty()) {
                        DayPlanCard(dayName = day.day, sessions = day.sessions)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PlannerHero() {
    GradientHero(brush = HeroBrushes.planner, modifier = Modifier.height(180.dp)) {
        Column {
            Text("Powered by AI ✦", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Text("Your Personalized", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Study Plan", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Add subjects & free time below", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun PlannerStat(emoji: String, value: String, label: String, bg: Color, accent: Color, modifier: Modifier) {
    Surface(modifier = modifier.height(120.dp), shape = RoundedCornerShape(18.dp), color = bg) {
        Column(
            Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(8.dp))
            Text(value, color = accent, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = InkMuted, fontSize = 13.sp)
        }
    }
}

// 24H time options in 30-minute increments: "00:00", "00:30", ..., "23:30"
private val timeOptions: List<String> = (0 until 24).flatMap { hour ->
    listOf("%02d:00".format(hour), "%02d:30".format(hour))
}

@Composable
private fun DayRow(dayInput: DayInput, onToggle: (Boolean) -> Unit, onStart: (String) -> Unit, onEnd: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = dayInput.isFree, onCheckedChange = onToggle)
            Text(dayInput.day, color = InkNavy, fontSize = 15.sp)
        }
        if (dayInput.isFree) {
            Row(
                modifier = Modifier.padding(start = 40.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TimeDropdownField(
                    label = "From",
                    selected = dayInput.startTime,
                    options = timeOptions,
                    onSelected = onStart,
                    modifier = Modifier.width(120.dp)
                )
                TimeDropdownField(
                    label = "To",
                    selected = dayInput.endTime,
                    options = timeOptions,
                    onSelected = onEnd,
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun TimeDropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {}, // read-only: selection happens via the menu
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedTextColor = InkNavy,
                unfocusedTextColor = InkNavy,
                disabledTextColor = InkNavy
            ),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { time ->
                DropdownMenuItem(
                    text = { Text(time) },
                    onClick = {
                        onSelected(time)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DayPlanCard(dayName: String, sessions: List<com.example.studyplannerapp.network.models.StudySession>) {
    Surface(shape = RoundedCornerShape(16.dp), color = CardWhite, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            sessions.forEachIndexed { idx, session ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(PurpleTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(dayName.take(3), color = BrandPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.size(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${session.subject}: ${session.topic}", color = InkNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("${session.startTime} – ${session.endTime}", color = InkMuted, fontSize = 13.sp)
                    }
                    Surface(shape = RoundedCornerShape(50), color = PurpleTint) {
                        Text(session.studyMethod, color = BrandPurple, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                if (idx < sessions.size - 1) Spacer(Modifier.height(10.dp))
            }
        }
    }
}