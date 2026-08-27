package com.example.studyplannerapp.network.models

import com.google.gson.annotations.SerializedName

// No request body model needed: /summarize-notes takes the file as multipart form data.

data class NotesResponse(val result: QuickNotes)

data class QuickNotes(
    val title: String,
    val summary: String,
    @SerializedName("key_points") val keyPoints: List<String>
)
