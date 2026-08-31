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
import androidx.compose.foundation.lazy.items
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
import com.example.studyplannerapp.ui.theme.BrandPurple
import com.example.studyplannerapp.ui.theme.CardWhite
import com.example.studyplannerapp.ui.theme.InkMuted
import com.example.studyplannerapp.ui.theme.InkNavy
import com.example.studyplannerapp.ui.theme.PurpleTint

@Composable
fun NotesScreen(viewModel: NotesViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

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

            item { SectionHeader("My Documents") }
            item { Spacer(Modifier.height(12.dp)) }
            if (uiState.documents.isEmpty()) {
                item {
                    Text(
                        "No documents yet — upload one above to get started.",
                        color = InkMuted, fontSize = 14.sp
                    )
                }
            } else {
                items(uiState.documents, key = { it.id }) { doc ->
                    DocumentRow(
                        doc = doc,
                        expanded = uiState.expandedDocId == doc.id,
                        onClick = { viewModel.toggleExpanded(doc.id) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun UploadHero(onUpload: () -> Unit) {
    GradientHero(brush = HeroBrushes.document) {
        Column {
            Text("Powered by AI ✦", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Text("Let AI organise", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("your Syllabus", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Upload any PDF — AI summarises & tags it for you.",
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
private fun LoadingCard(message: String) {
    Surface(shape = RoundedCornerShape(16.dp), color = CardWhite, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = BrandPurple)
            Spacer(Modifier.size(12.dp))
            Text(message, color = InkNavy, fontSize = 14.sp)
        }
    }
}

// Now tappable: expands in place to show the AI summary + key points for
// real uploads, or an explanatory note for the placeholder sample rows.
@Composable
private fun DocumentRow(doc: SummarizedDocument, expanded: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(PurpleTint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Description, null, tint = BrandPurple, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.size(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(doc.title, color = InkNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Spacer(Modifier.height(4.dp))
                    Text(doc.meta, color = InkMuted, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (doc.notes != null) "• AI Summary ready" else "Tap to see details",
                        color = if (doc.notes != null) AccentGreen else InkMuted,
                        fontSize = 12.sp
                    )
                }
                Text(if (expanded) "▲" else "▼", color = InkMuted, fontSize = 14.sp)
            }

            if (expanded) {
                Spacer(Modifier.height(12.dp))
                if (doc.notes != null) {
                    Column {
                        Text(doc.notes.summary, color = InkNavy.copy(alpha = 0.8f), fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        doc.notes.keyPoints.forEach { p ->
                            Text("•  $p", color = InkNavy.copy(alpha = 0.8f), fontSize = 14.sp)
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                } else {
                    Text(
                        "This is a placeholder document — upload your own PDF above to generate a real AI summary.",
                        color = InkMuted, fontSize = 13.sp
                    )
                }
            }
        }
    }
}