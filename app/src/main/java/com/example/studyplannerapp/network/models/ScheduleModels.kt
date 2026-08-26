package com.example.studyplannerapp.network.models

data class ScheduleRequest(
    val subject: String,
    val hours_available_per_week: Int = 7
)

data class ScheduleResponse(val result: WeeklyPlan)

data class WeeklyPlan(
    val subject: String,
    val weekOverview: String,
    val days: List<StudyDay>
)

data class StudyDay(
    val day: String,
    val topic: String,
    val duration_minutes: Int,
    val study_method: String
)