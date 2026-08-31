package com.example.studyplannerapp.network.models

import com.google.gson.annotations.SerializedName

// No request body model needed: /summarize-notes takes the file as multipart form data.

data class NotesResponse(
    val result: QuickNotes,
    @SerializedName("page_count") val pageCount: Int? = null
)

data class QuickNotes(
    val title: String,
    val summary: String,
    @SerializedName("key_points") val keyPoints: List<String>
)

// What StudyRepository.summarizeNotes() actually hands back to the ViewModel
// — the AI result plus the page count computed server-side, bundled together
// since callers need both.
data class NotesResult(
    val notes: QuickNotes,
    val pageCount: Int?
)