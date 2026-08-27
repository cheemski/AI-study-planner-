package com.example.studyplannerapp.network.models

import com.google.gson.annotations.SerializedName

data class QuizRequest(
    val subject: String,
    @SerializedName("num_questions") val numQuestions: Int = 10 // server caps at 20
)

data class QuizResponse(val result: Quiz)

data class Quiz(
    @SerializedName("quiz_title") val quizTitle: String,
    val questions: List<QuizQuestion>
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,               // always exactly 4
    @SerializedName("correct_answer") val correctAnswer: String, // matches one of `options` exactly
    val explanation: String
)
