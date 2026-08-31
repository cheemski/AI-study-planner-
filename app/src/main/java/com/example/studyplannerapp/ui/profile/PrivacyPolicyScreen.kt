package com.example.studyplannerapp.ui.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyplannerapp.ui.components.CircleBackButton
import com.example.studyplannerapp.ui.components.ScreenBackground
import com.example.studyplannerapp.ui.theme.InkMuted
import com.example.studyplannerapp.ui.theme.InkNavy

// Placeholder copy — swap in real policy text before shipping beyond a school project.
private val placeholderSections = listOf(
    "Information We Collect" to "This app stores your account email, uploaded documents, and study preferences to provide its features.",
    "How We Use It" to "Your data is used only to generate summaries, quizzes, and schedules through the AI features of this app.",
    "Data Storage" to "Documents and account data are stored via Supabase and are not shared with third parties.",
    "Contact" to "For questions about this policy, contact the app developer directly."
)

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    ScreenBackground {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleBackButton(onClick = onBack)
                    Spacer(Modifier.size(16.dp))
                    Text("Privacy Policy", color = InkNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(24.dp))
            }
            items(placeholderSections) { (title, body) ->
                Text(title, color = InkNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(body, color = InkMuted, fontSize = 14.sp)
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
