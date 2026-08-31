package com.example.studyplannerapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyplannerapp.data.Task
import com.example.studyplannerapp.network.SupabaseClient
import com.example.studyplannerapp.repository.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

class HomeViewModel(
    private val repository: TasksRepository = TasksRepository()
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Same lookup as ProfileViewModel.loadCurrentUser: prefer the metadata
    // full_name, fall back to the email's local part, then a generic greeting
    // if somehow neither is available.
    private val _displayName = MutableStateFlow(loadDisplayName())
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private fun loadDisplayName(): String {
        val user = SupabaseClient.auth.currentUserOrNull()
        val metadataName = user?.userMetadata?.get("full_name")?.jsonPrimitive?.content
        return metadataName?.takeIf { it.isNotBlank() }
            ?: user?.email?.substringBefore("@")
            ?: "there"
    }

    init {
        refresh()
    }

    private fun refresh() = viewModelScope.launch {
        repository.getTodaysTasks()
            .onSuccess { _tasks.value = it }
            .onFailure { _errorMessage.value = it.message ?: "Could not load tasks" }
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(title.trim())
                .onSuccess { refresh() }
                .onFailure { _errorMessage.value = it.message ?: "Could not add task" }
        }
    }

    fun toggleTask(id: String) {
        val current = _tasks.value.find { it.id == id } ?: return
        viewModelScope.launch {
            repository.setDone(id, !current.done)
                .onSuccess { refresh() }
                .onFailure { _errorMessage.value = it.message ?: "Could not update task" }
        }
    }

    fun removeTask(id: String) {
        viewModelScope.launch {
            repository.deleteTask(id)
                .onSuccess { refresh() }
                .onFailure { _errorMessage.value = it.message ?: "Could not remove task" }
        }
    }
}