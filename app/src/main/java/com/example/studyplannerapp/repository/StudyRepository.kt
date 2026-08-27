package com.example.studyplannerapp.repository

import com.example.studyplannerapp.network.APIService
import com.example.studyplannerapp.network.RetrofitClient
import com.example.studyplannerapp.network.models.DayAvailability
import com.example.studyplannerapp.network.models.QuickNotes
import com.example.studyplannerapp.network.models.Quiz
import com.example.studyplannerapp.network.models.QuizRequest
import com.example.studyplannerapp.network.models.ScheduleRequest
import com.example.studyplannerapp.network.models.WeeklyPlan
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Thin layer between the ViewModels and the network. Every call returns a
 * Result so the UI can show an error message instead of crashing when the
 * AI server is unreachable or slow.
 */
class StudyRepository(
    private val api: APIService = RetrofitClient.api
) {

    suspend fun getSchedule(subjects: List<String>, availability: List<DayAvailability>): Result<WeeklyPlan> =
        runCatching {
            api.createSchedule(ScheduleRequest(subjects, availability)).result
        }

    suspend fun getQuiz(subject: String, numQuestions: Int = 10): Result<Quiz> =
        runCatching {
            api.createQuiz(QuizRequest(subject, numQuestions)).result
        }

    // `file` should already be a local, readable copy (see NotesViewModel for
    // how a content:// Uri picked by the user gets copied into one).
    suspend fun summarizeNotes(file: File): Result<QuickNotes> =
        runCatching {
            val mediaType = if (file.extension.lowercase() == "pdf") "application/pdf" else "text/plain"
            val body = file.asRequestBody(mediaType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, body)
            api.summarizeNotes(part).result
        }
}
