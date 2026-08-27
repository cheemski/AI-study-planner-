package com.example.studyplannerapp.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyplannerapp.network.models.Quiz
import com.example.studyplannerapp.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val subject: String = "",
    val numQuestions: Int = 10,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val quiz: Quiz? = null,
    // Quiz-taking state
    val currentIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerRevealed: Boolean = false,
    val correctCount: Int = 0,
    val isFinished: Boolean = false
)

class QuizViewModel(
    private val repository: StudyRepository = StudyRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun onSubjectChange(value: String) = _uiState.update { it.copy(subject = value, errorMessage = null) }

    fun onNumQuestionsChange(value: Int) = _uiState.update { it.copy(numQuestions = value.coerceIn(1, 20)) }

    fun generateQuiz() = viewModelScope.launch {
        val state = _uiState.value
        if (state.subject.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter a topic first") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        repository.getQuiz(state.subject, state.numQuestions)
            .onSuccess { quiz ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        quiz = quiz,
                        currentIndex = 0,
                        selectedAnswer = null,
                        isAnswerRevealed = false,
                        correctCount = 0,
                        isFinished = false
                    )
                }
            }
            .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Could not generate a quiz") } }
    }

    fun selectAnswer(answer: String) {
        val state = _uiState.value
        if (state.isAnswerRevealed) return
        val question = state.quiz?.questions?.getOrNull(state.currentIndex) ?: return
        val isCorrect = answer == question.correctAnswer
        _uiState.update {
            it.copy(
                selectedAnswer = answer,
                isAnswerRevealed = true,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount
            )
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        val total = state.quiz?.questions?.size ?: 0
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= total) {
            _uiState.update { it.copy(isFinished = true) }
        } else {
            _uiState.update { it.copy(currentIndex = nextIndex, selectedAnswer = null, isAnswerRevealed = false) }
        }
    }

    // Start over with a new topic.
    fun reset() = _uiState.update { QuizUiState() }
}
