package com.example.studyplannerapp.ui.notes

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyplannerapp.network.models.QuickNotes
import com.example.studyplannerapp.repository.StudyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class NotesUiState(
    val fileName: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val notes: QuickNotes? = null
)

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudyRepository()

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    fun onFilePicked(uri: Uri) = viewModelScope.launch {
        val context = getApplication<android.app.Application>().applicationContext
        val fileName = queryFileName(uri) ?: "upload"
        _uiState.update { it.copy(isLoading = true, errorMessage = null, fileName = fileName, notes = null) }

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
            .onSuccess { notes -> _uiState.update { it.copy(isLoading = false, notes = notes) } }
            .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Could not summarize this file") } }
    }

    fun reset() = _uiState.update { NotesUiState() }

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
