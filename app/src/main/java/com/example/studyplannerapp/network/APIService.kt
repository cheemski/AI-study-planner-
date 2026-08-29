package com.example.studyplannerapp.network

import com.example.studyplannerapp.network.models.NotesResponse
import com.example.studyplannerapp.network.models.QuizRequest
import com.example.studyplannerapp.network.models.QuizResponse
import com.example.studyplannerapp.network.models.ScheduleRequest
import com.example.studyplannerapp.network.models.ScheduleResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Matches the FastAPI routes in AI Server/AI server/main.py exactly.
 * AI responses can take several seconds, hence the generous timeouts in
 * RetrofitClient.
 */
interface APIService {

    // Function 1: weekly study schedule built only inside the student's free time windows.
    @POST("schedule")
    suspend fun createSchedule(@Body request: ScheduleRequest): ScheduleResponse

    // Function 2: up to 20 multiple-choice questions for a subject.
    @POST("quiz")
    suspend fun createQuiz(@Body request: QuizRequest): QuizResponse

    // Function 3: upload a PDF/text file, get back a short summary + key points.
    @Multipart
    @POST("summarize-notes")
    suspend fun summarizeNotes(@Part file: MultipartBody.Part): NotesResponse
}
