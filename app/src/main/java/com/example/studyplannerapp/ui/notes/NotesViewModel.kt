package com.example.studyplannerapp.ui.notes

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyplannerapp.network.models.QuickNotes
import com.example.studyplannerapp.repository.DocumentsRepository
import com.example.studyplannerapp.repository.StudyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

data class SummarizedDocument(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val meta: String,
    // null = placeholder row with no real summary behind it yet
    val notes: QuickNotes?
)

data class NotesUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val documents: List<SummarizedDocument> = emptyList(),
    val expandedDocId: String? = null
)

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudyRepository()
    private val documentsRepository = DocumentsRepository()

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        loadSavedDocuments()
    }

    private fun loadSavedDocuments() = viewModelScope.launch {
        documentsRepository.getDocuments()
            .onSuccess { docs -> _uiState.update { it.copy(documents = docs) } }
            .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message ?: "Could not load saved documents") } }
    }

    fun onFilePicked(uri: Uri) = viewModelScope.launch {
        val context = getApplication<android.app.Application>().applicationContext
        val fileName = queryFileName(uri) ?: "upload"
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // Copy the content:// stream into a real file, since Retrofit's
        // multipart body needs a File it can read from disk.
        val localFile = withContext(Dispatchers.IO) {
            val out = File(context.cacheDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                out.outputStream().use { output -> input.copyTo(output) }
            }
            out
        }

        repository.summarizeNotes(localFile)
            .onSuccess { result ->
                val notes = result.notes
                val sizeKb = localFile.length() / 1024
                val sizeLabel = if (sizeKb > 1024) "%.1f MB".format(sizeKb / 1024.0) else "$sizeKb KB"
                val newDoc = SummarizedDocument(title = notes.title, meta = buildMeta(sizeLabel, result.pageCount), notes = notes)

                // Show it immediately, then persist in the background — the
                // document stays visible in this session even if the save
                // call fails, but won't survive logout/login in that case.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        documents = listOf(newDoc) + it.documents,
                        expandedDocId = newDoc.id
                    )
                }

                documentsRepository.saveDocument(
                    title = notes.title,
                    summary = notes.summary,
                    keyPoints = notes.keyPoints,
                    pages = result.pageCount,
                    sizeLabel = sizeLabel
                ).onFailure { e ->
                    _uiState.update { it.copy(errorMessage = "Summarized, but couldn't save it: ${e.message}") }
                }
            }
            .onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Could not summarize this file") }
            }
    }

    // Tapping the same row again collapses it; tapping a different row
    // switches which one is open.
    fun toggleExpanded(docId: String) = _uiState.update {
        it.copy(expandedDocId = if (it.expandedDocId == docId) null else docId)
    }

    private fun queryFileName(uri: Uri): String? {
        val context = getApplication<android.app.Application>().applicationContext
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
}

private fun buildMeta(sizeLabel: String, pages: Int?): String =
    listOfNotNull(sizeLabel, pages?.let { "$it pages" }).joinToString(" · ")