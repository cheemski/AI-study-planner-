package com.example.studyplannerapp.repository

import com.example.studyplannerapp.network.APIService
import com.example.studyplannerapp.network.RetrofitClient
import com.example.studyplannerapp.network.SupabaseClient
import com.example.studyplannerapp.network.models.DayAvailability
import com.example.studyplannerapp.network.models.QuickNotes
import com.example.studyplannerapp.network.models.Quiz
import com.example.studyplannerapp.network.models.QuizRequest
import com.example.studyplannerapp.network.models.ScheduleRequest
import com.example.studyplannerapp.network.models.ScheduleRow
import com.example.studyplannerapp.network.models.WeeklyPlan
import com.example.studyplannerapp.ui.schedule.DayInput
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import android.util.Log
import com.example.studyplannerapp.network.models.NotesResult
import java.io.File
import java.util.UUID

/** What a user previously saved, reloaded on next login. */
data class SavedSchedule(
    val subjectsText: String,
    val dayInputs: List<DayInput>,
    val plan: WeeklyPlan?
)

/** One row shown in the "Recent Quizzes" list. */
data class QuizResultSummary(
    val title: String,
    val numQuestions: Int,
    val scorePercent: Int
)

// Row shape matching the `quiz_results` table (see create_quiz_results_table.sql).
// Same caveat as elsewhere: the exact Postgrest select/insert DSL is
// version-dependent in supabase-kt — adjust if your installed version's
// syntax differs.
@Serializable
private data class QuizResultRow(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String,
    val title: String,
    @SerialName("num_questions") val numQuestions: Int,
    @SerialName("score_percent") val scorePercent: Int
)

/**
 * Thin layer between the ViewModels and the network. Every call returns a
 * Result so the UI can show an error message instead of crashing when the
 * AI server is unreachable or slow.
 */
class StudyRepository(
    private val api: APIService = RetrofitClient.api
) {
    private val gson = Gson()

    suspend fun getSchedule(subjects: List<String>, availability: List<DayAvailability>): Result<WeeklyPlan> =
        runCatching {
            api.createSchedule(ScheduleRequest(subjects, availability)).result
        }

    // Upserts the caller's one row in `schedules`, keyed by their user id.
    // Fails via Result if the user isn't logged in or the request fails;
    // logged so a silent failure here is at least visible in Logcat.
    suspend fun saveSchedule(subjectsText: String, dayInputs: List<DayInput>, plan: WeeklyPlan?): Result<Unit> =
        runCatching {
            val userId = SupabaseClient.auth.currentUserOrNull()?.id
                ?: error("Not logged in")
            val row = ScheduleRow(
                userId = userId,
                subjectsText = subjectsText,
                dayInputsJson = gson.toJson(dayInputs),
                planJson = plan?.let { gson.toJson(it) }
            )
            // onConflict has no fixed spot across postgrest-kt versions, so we
            // rely on PostgREST's default behavior instead: with no on_conflict
            // column specified, it resolves against the table's primary key —
            // which is user_id here — so this still overwrites the existing row
            // rather than erroring on the second save.
            SupabaseClient.postgrest["schedules"].upsert(listOf(row))
            Unit
        }.onFailure { e -> Log.e("StudyRepository", "saveSchedule failed", e) }

    // Returns null (not a failure) if the user is logged out or has never saved one.
    suspend fun loadSchedule(): Result<SavedSchedule?> =
        runCatching {
            val userId = SupabaseClient.auth.currentUserOrNull()?.id ?: return@runCatching null
            val row = SupabaseClient.postgrest["schedules"]
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeSingleOrNull<ScheduleRow>() ?: return@runCatching null

            val dayInputsType = object : TypeToken<List<DayInput>>() {}.type
            val dayInputs: List<DayInput> = gson.fromJson(row.dayInputsJson, dayInputsType)
            val plan: WeeklyPlan? = row.planJson?.let { gson.fromJson(it, WeeklyPlan::class.java) }

            SavedSchedule(subjectsText = row.subjectsText, dayInputs = dayInputs, plan = plan)
        }.onFailure { e -> Log.e("StudyRepository", "loadSchedule failed", e) }

    suspend fun getQuiz(subject: String, numQuestions: Int = 10): Result<Quiz> =
        runCatching {
            api.createQuiz(QuizRequest(subject, numQuestions)).result
        }

    // Inserts one row per completed attempt — unlike `schedules`, quiz history
    // is append-only, so this is a plain insert rather than an upsert.
    suspend fun saveQuizResult(title: String, numQuestions: Int, scorePercent: Int): Result<Unit> =
        runCatching {
            val userId = SupabaseClient.auth.currentUserOrNull()?.id
                ?: error("Not logged in")
            val row = QuizResultRow(
                userId = userId,
                title = title,
                numQuestions = numQuestions,
                scorePercent = scorePercent
            )
            SupabaseClient.postgrest["quiz_results"].insert(row)
            Unit
        }.onFailure { e -> Log.e("StudyRepository", "saveQuizResult failed", e) }

    // Returns an empty list (not a failure) if the user is logged out.
    suspend fun getRecentQuizResults(limit: Int = 5): Result<List<QuizResultSummary>> =
        runCatching {
            val userId = SupabaseClient.auth.currentUserOrNull()?.id ?: return@runCatching emptyList()
            SupabaseClient.postgrest["quiz_results"]
                .select {
                    filter { eq("user_id", userId) }
                    order("created_at", order = Order.DESCENDING)
                    limit(limit.toLong())
                }
                .decodeList<QuizResultRow>()
                .map { QuizResultSummary(title = it.title, numQuestions = it.numQuestions, scorePercent = it.scorePercent) }
        }.onFailure { e -> Log.e("StudyRepository", "getRecentQuizResults failed", e) }

    // `file` should already be a local, readable copy (see NotesViewModel for
    // how a content:// Uri picked by the user gets copied into one).
    suspend fun summarizeNotes(file: File): Result<NotesResult> =
        runCatching {
            val mediaType = if (file.extension.lowercase() == "pdf") "application/pdf" else "text/plain"
            val body = file.asRequestBody(mediaType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, body)
            val response = api.summarizeNotes(part)
            NotesResult(notes = response.result, pageCount = response.pageCount)
        }
}