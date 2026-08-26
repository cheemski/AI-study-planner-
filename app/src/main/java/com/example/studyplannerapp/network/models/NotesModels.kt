package com.example.studyplannerapp.network.models

data class NotesResponse(val result: QuickNotes)

data class QuickNotes(
    val title: String,
    val summary: String,
    val keyPoints: List<String>
)