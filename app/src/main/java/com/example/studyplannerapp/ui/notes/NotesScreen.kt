package com.example.studyplannerapp.ui.notes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.example.studyplannerapp.ui.components.StatCard
import com.example.studyplannerapp.ui.theme.AccentGreen
import com.example.studyplannerapp.ui.theme.AccentOrange
import com.example.studyplannerapp.ui.theme.BrandPurple
import com.example.studyplannerapp.ui.theme.CardWhite
import com.example.studyplannerapp.ui.theme.GreenTint
import com.example.studyplannerapp.ui.theme.InkMuted
import com.example.studyplannerapp.ui.theme.InkNavy
import com.example.studyplannerapp.ui.theme.PurpleTint

// Sample rows shown for layout context. The backend summarises one uploaded
// file at a time (no document-list endpoint yet), so these are placeholders;
// a real upload result appears as a highlighted card above them.
private data class DocRow(val title: String, val code: String, val meta: String, val tint: Color)

private val sampleDocs = listOf(
    DocRow("Operating System Lecture Notes", "CS301", "2.4 MB · 48 pages", PurpleTint),
    DocRow("Marketing Strategy Essay", "MKT201", "820 KB · 12 pages", GreenTint),
    DocRow("Calculus Chapter 6 Slides", "MATH102", "3.1 MB · 32 pages", Color(0xFFFFF3E0))
)

@Composable
fun NotesScreen(viewModel: NotesViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var filter by remember { mutableStateOf("All") }

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.onFilePicked(it) } }

    ScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
        ) {
            item {
                Column {
                    Text("Your Library", color = InkMuted, fontSize = 14.sp)
                    Text("AI Document", color = InkNavy, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
            item { UploadHero(onUpload = { pickFileLauncher.launch("*/*") }) }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("5", "Documents", BrandPurple, Modifier.weight(1f))
                    StatCard("3", "Summarised", AccentGreen, Modifier.weight(1f))
                    StatCard("5", "Subjects", AccentOrange, Modifier.weight(1f))
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("All", "PDF", "Notes", "Slides").forEach { f ->
                        FilterChip(f, selected = filter == f) { filter = f }
                    }
                }
            }
            item { Spacer(Modifier.height(20.dp)) }

            if (uiState.isLoading) {
                item { LoadingCard("Reading and summarising…") }
                item { Spacer(Modifier.height(16.dp)) }
            }
            uiState.errorMessage?.let {
                item {
                    Text(it, color = com.example.studyplannerapp.ui.theme.AccentRed, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                }
            }
            uiState.notes?.let { notes ->
                item { SummaryResultCard(title = notes.title, summary = notes.summary, points = notes.keyPoints) }
                item { Spacer(Modifier.height(20.dp)) }
            }

            item { SectionHeader("My Documents") }
            item { Spacer(Modifier.height(12.dp)) }
            items(sampleDocs.size) { i ->
                DocumentRow(sampleDocs[i])
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun UploadHero(onUpload: () -> Unit) {
    GradientHero(brush = HeroBrushes.document, modifier = Modifier.height(220.dp)) {
        Column {
            Text("Powered by AI ✦", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Text("Let AI organise", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("your Syllabus", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Upload any PDF, slide, or note — AI summarises & tags it for you.",
                color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp
            )
            Spacer(Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                modifier = Modifier.clickable { onUpload() }
            ) {
                Text(
                    "Upload Document",
                    color = BrandPurple, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) BrandPurple else CardWhite,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            label,
            color = if (selected) Color.White else InkMuted,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun LoadingCard(message: String) {
    Surface(shape = RoundedCornerShape(16.dp), color = CardWhite, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = BrandPurple)
            Spacer(Modifier.size(12.dp))
            Text(message, color = InkNavy, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SummaryResultCard(title: String, summary: String, points: List<String>) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CardWhite,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentGreen),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Text("• AI Summary ready", color = AccentGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(title, color = InkNavy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(summary, color = InkNavy.copy(alpha = 0.8f), fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            points.forEach { p ->
                Text("•  $p", color = InkNavy.copy(alpha = 0.8f), fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun DocumentRow(doc: DocRow) {
    Surface(shape = RoundedCornerShape(16.dp), color = CardWhite, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(doc.tint),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Description, null, tint = BrandPurple, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text(doc.title, color = InkNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(50), color = PurpleTint) {
                        Text(doc.code, color = BrandPurple, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                    Spacer(Modifier.size(8.dp))
                    Text(doc.meta, color = InkMuted, fontSize = 12.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text("• AI Summary ready", color = AccentGreen, fontSize = 12.sp)
            }
        }
    }
}
