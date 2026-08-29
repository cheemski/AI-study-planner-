package com.example.studyplannerapp.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyplannerapp.network.models.DayAvailability
import com.example.studyplannerapp.network.models.TimeSlot
import com.example.studyplannerapp.network.models.WeeklyPlan
import com.example.studyplannerapp.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val WEEK_DAYS = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

// One row of user input per day: is the student free, and during what window.
data class DayInput(
    val day: String,
    val isFree: Boolean = false,
    val startTime: String = "16:00",
    val endTime: String = "18:00"
)

data class ScheduleUiState(
    val subjectsText: String = "",
    val dayInputs: List<DayInput> = WEEK_DAYS.map { DayInput(it) },
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val plan: WeeklyPlan? = null
)

class ScheduleViewModel(
    private val repository: StudyRepository = StudyRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    fun onSubjectsChange(value: String) = _uiState.update { it.copy(subjectsText = value, errorMessage = null) }

    fun onDayToggle(day: String, isFree: Boolean) = _uiState.update { state ->
        state.copy(dayInputs = state.dayInputs.map { if (it.day == day) it.copy(isFree = isFree) else it })
    }

    fun onStartTimeChange(day: String, value: String) = _uiState.update { state ->
        state.copy(dayInputs = state.dayInputs.map { if (it.day == day) it.copy(startTime = value) else it })
    }

    fun onEndTimeChange(day: String, value: String) = _uiState.update { state ->
        state.copy(dayInputs = state.dayInputs.map { if (it.day == day) it.copy(endTime = value) else it })
    }

    fun generatePlan() = viewModelScope.launch {
        val state = _uiState.value
        val subjects = state.subjectsText.split(",").map { it.trim() }.filter { it.isNotBlank() }

        if (subjects.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Add at least one subject (comma separated)") }
            return@launch
        }
        val freeDays = state.dayInputs.filter { it.isFree }
        if (freeDays.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Mark at least one day as free") }
            return@launch
        }

        val availability = state.dayInputs.map { input ->
            DayAvailability(
                day = input.day,
                freeTimes = if (input.isFree) listOf(TimeSlot(input.startTime, input.endTime)) else emptyList()
            )
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        repository.getSchedule(subjects, availability)
            .onSuccess { plan -> _uiState.update { it.copy(isLoading = false, plan = plan) } }
            .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Could not generate a plan") } }
    }
}
