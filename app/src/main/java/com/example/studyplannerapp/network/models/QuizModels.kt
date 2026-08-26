package com.example.studyplannerapp.network.models

data class QuizRequest(
    val subject: String,
    val numQuestions: Int = 10
)

data class QuizResponse(
    val result: Quiz
)

data class Quiz(
    val quizTitle: String,
    val questions: List<QuizQuestion>
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)