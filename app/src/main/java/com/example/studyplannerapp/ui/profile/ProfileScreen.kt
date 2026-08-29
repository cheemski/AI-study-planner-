package com.example.studyplannerapp.ui.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyplannerapp.ui.components.CircleBackButton
import com.example.studyplannerapp.ui.components.ScreenBackground
import com.example.studyplannerapp.ui.components.StatCard
import com.example.studyplannerapp.ui.theme.AccentGreen
import com.example.studyplannerapp.ui.theme.AccentOrange
import com.example.studyplannerapp.ui.theme.AccentRed
import com.example.studyplannerapp.ui.theme.BrandPurple
import com.example.studyplannerapp.ui.theme.CardWhite
import com.example.studyplannerapp.ui.theme.InkMuted
import com.example.studyplannerapp.ui.theme.InkNavy
import com.example.studyplannerapp.ui.theme.RedTint

@Composable
fun ProfileScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    // TODO: wire to the real Supabase session email once the app runs.
    val email = "alex.tan@university.edu"

    ScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleBackButton(onClick = onBack)
                    Spacer(Modifier.size(16.dp))
                    Text("My Profile", color = InkNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
            item { ProfileHeader(email = email) }
            item { Spacer(Modifier.height(20.dp)) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("5", "Documents", BrandPurple, Modifier.weight(1f))
                    StatCard("4", "Quizzes", AccentRed, Modifier.weight(1f))
                    StatCard("5d", "Streak", AccentOrange, Modifier.weight(1f))
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
            item { AchievementsCard() }
            item { Spacer(Modifier.height(20.dp)) }

            item { SettingsGroup("ACCOUNT") }
            item {
                SettingsCard(
                    listOf(
                        Triple("Edit Profile", "Update your name & photo", true),
                        Triple("Email Address", email, true),
                        Triple("Change Password", "Last changed 30 days ago", true)
                    )
                )
                Spacer(Modifier.height(20.dp))
            }

            item { SettingsGroup("PREFERENCES") }
            item {
                PreferencesCard()
                Spacer(Modifier.height(20.dp))
            }

            item { SettingsGroup("ABOUT") }
            item {
                SettingsCard(
                    listOf(
                        Triple("Privacy Policy", "How we protect your data", true),
                        Triple("Help & Support", "FAQ & contact us", true)
                    )
                )
                Spacer(Modifier.height(24.dp))
            }

            item {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = RedTint),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed)
                ) {
                    Text("Log Out", color = AccentRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(email: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(
            Modifier.size(110.dp).clip(RoundedCornerShape(28.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF9B2FE0), Color(0xFF6B49EC)))),
            contentAlignment = Alignment.Center
        ) {
            Text("A", color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text("Alex Tan", color = InkNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(email, color = BrandPurple, fontSize = 15.sp)
        Spacer(Modifier.height(4.dp))
        Text("• Computer Science · Year 2", color = AccentGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AchievementsCard() {
    Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("Achievements", color = InkNavy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Badge("🔥", "5-Day Streak", Color(0xFFFFE5E5), Modifier.weight(1f))
                Badge("📚", "5 Docs", Color(0xFFEDE9FF), Modifier.weight(1f))
                Badge("🧠", "Quiz Master", Color(0xFFE0FDF4), Modifier.weight(1f))
                Badge("⭐", "Top Scorer", Color(0xFFFFF7D6), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Badge(emoji: String, label: String, bg: Color, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(bg),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 24.sp) }
        Spacer(Modifier.height(6.dp))
        Text(label, color = InkNavy, fontSize = 11.sp, fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun SettingsGroup(title: String) {
    Text(title, color = InkMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 10.dp))
}

@Composable
private fun SettingsCard(rows: List<Triple<String, String, Boolean>>) {
    Surface(shape = RoundedCornerShape(18.dp), color = CardWhite, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column {
            rows.forEachIndexed { i, (title, sub, _) ->
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(Color(0xFFEDE9FF)))
                    Spacer(Modifier.size(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(title, color = InkNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(sub, color = InkMuted, fontSize = 13.sp, maxLines = 1)
                    }
                    Text("›", color = InkMuted, fontSize = 22.sp)
                }
                if (i < rows.size - 1) Divider(color = Color(0xFFF0EEF8), thickness = 1.dp)
            }
        }
    }
}

@Composable
private fun PreferencesCard() {
    var notifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }
    Surface(shape = RoundedCornerShape(18.dp), color = CardWhite, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column {
            ToggleRow("Notification", "Enable", notifications) { notifications = it }
            Divider(color = Color(0xFFF0EEF8), thickness = 1.dp)
            ToggleRow("Dark Mode", if (darkMode) "On" else "Off", darkMode) { darkMode = it }
            Divider(color = Color(0xFFF0EEF8), thickness = 1.dp)
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(Color(0xFFEDE9FF)))
                Spacer(Modifier.size(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("Language", color = InkNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("English", color = InkMuted, fontSize = 13.sp)
                }
                Text("›", color = InkMuted, fontSize = 22.sp)
            }
        }
    }
}

@Composable
private fun ToggleRow(title: String, sub: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(Color(0xFFEDE9FF)))
        Spacer(Modifier.size(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = InkNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = InkMuted, fontSize = 13.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandPurple
            )
        )
    }
}
