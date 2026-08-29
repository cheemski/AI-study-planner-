package com.example.studyplannerapp.network.models

import com.google.gson.annotations.SerializedName

// ---- Request ----

data class TimeSlot(
    @SerializedName("start_time") val startTime: String, // "HH:MM" 24hr, e.g. "16:00"
    @SerializedName("end_time") val endTime: String
)

data class DayAvailability(
    val day: String,                                       // e.g. "Monday"
    @SerializedName("free_times") val freeTimes: List<TimeSlot> // empty if not free that day
)

data class ScheduleRequest(
    val subjects: List<String>,
    val availability: List<DayAvailability>
)

// ---- Response ----

data class ScheduleResponse(val result: WeeklyPlan)

data class WeeklyPlan(
    val subjects: List<String>,
    @SerializedName("week_overview") val weekOverview: String,
    val days: List<StudyDay>
)

data class StudyDay(
    val day: String,
    val sessions: List<StudySession>
)

data class StudySession(
    val subject: String,
    val topic: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("study_method") val studyMethod: String
)
