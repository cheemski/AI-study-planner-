package com.example.studyplannerapp.repository

import com.example.studyplannerapp.data.Task
import com.example.studyplannerapp.network.SupabaseClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// Row shape matching the `tasks` table in Supabase (see create_tasks_table.sql).
// user_id/created_date are DB-only concerns kept out of the UI-facing Task
// model in data/Task.kt.
@Serializable
private data class TaskRow(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String,
    val title: String,
    val done: Boolean = false,
    @SerialName("created_date") val createdDate: String
)

private fun todayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

// NOTE: the exact Postgrest DSL (select/insert/update/delete syntax) is
// version-dependent in supabase-kt. This matches the common v2/v3 shape —
// if your installed version's `update` builder looks different, check its
// docs for the filter{}/set{} syntax and adjust setDone() specifically.
// Everything else here (table name, RLS policies, this class's public API)
// stays the same regardless of that detail.
class TasksRepository {

    private val table get() = SupabaseClient.postgrest.from("tasks")

    // Only fetches rows created today — this is what makes the list
    // "clear" on a new day without deleting anything. Yesterday's tasks
    // are still in the table, just not queried for.
    suspend fun getTodaysTasks(): Result<List<Task>> = runCatching {
        val userId = SupabaseClient.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Not logged in")
        table.select {
            filter {
                eq("user_id", userId)
                eq("created_date", todayKey())
            }
        }.decodeList<TaskRow>().map { Task(id = it.id, title = it.title, done = it.done) }
    }

    suspend fun addTask(title: String): Result<Unit> = runCatching {
        val userId = SupabaseClient.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Not logged in")
        table.insert(TaskRow(userId = userId, title = title, createdDate = todayKey()))
    }

    suspend fun setDone(id: String, done: Boolean): Result<Unit> = runCatching {
        table.update({ set("done", done) }) {
            filter { eq("id", id) }
        }
    }

    suspend fun deleteTask(id: String): Result<Unit> = runCatching {
        table.delete { filter { eq("id", id) } }
    }
}
