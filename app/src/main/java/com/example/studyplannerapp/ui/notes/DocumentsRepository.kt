package com.example.studyplannerapp.repository

import com.example.studyplannerapp.network.SupabaseClient
import com.example.studyplannerapp.network.models.QuickNotes
import com.example.studyplannerapp.ui.notes.SummarizedDocument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
import io.github.jan.supabase.postgrest.query.Order

// Row shape matching the `documents` table in Supabase (see
// create_documents_table.sql). See TasksRepository for the same caveat: the
// exact Postgrest select/insert DSL is version-dependent in supabase-kt —
// adjust if your installed version's syntax differs.
@Serializable
private data class DocumentRow(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String,
    val title: String,
    val summary: String,
    @SerialName("key_points") val keyPoints: List<String>,
    val pages: Int? = null,
    @SerialName("size_label") val sizeLabel: String? = null
)

class DocumentsRepository {

    private val table get() = SupabaseClient.postgrest.from("documents")

    suspend fun getDocuments(): Result<List<SummarizedDocument>> = runCatching {
        val userId = SupabaseClient.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Not logged in")
        table.select {
            filter { eq("user_id", userId) }
            order("created_at", order = Order.DESCENDING)
        }.decodeList<DocumentRow>().map { row ->
            SummarizedDocument(
                id = row.id,
                title = row.title,
                meta = buildMeta(row.sizeLabel, row.pages),
                notes = QuickNotes(title = row.title, summary = row.summary, keyPoints = row.keyPoints)
            )
        }
    }

    suspend fun saveDocument(
        title: String,
        summary: String,
        keyPoints: List<String>,
        pages: Int?,
        sizeLabel: String?
    ): Result<Unit> = runCatching {
        val userId = SupabaseClient.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Not logged in")
        table.insert(
            DocumentRow(
                userId = userId,
                title = title,
                summary = summary,
                keyPoints = keyPoints,
                pages = pages,
                sizeLabel = sizeLabel
            )
        )
    }
}

private fun buildMeta(sizeLabel: String?, pages: Int?): String =
    listOfNotNull(sizeLabel, pages?.let { "$it pages" }).joinToString(" · ")
