package com.example.studyplannerapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One row per user in the `schedules` table. `dayInputsJson` and `planJson`
 * hold Gson-serialized JSON (as plain text columns) so we can reuse the
 * existing Gson-annotated models (DayInput, WeeklyPlan) without also wiring
 * them up for kotlinx.serialization, which is what the Supabase client uses
 * for its own request/response bodies.
 */
@Serializable
data class ScheduleRow(
    @SerialName("user_id") val userId: String,
    @SerialName("subjects_text") val subjectsText: String,
    @SerialName("day_inputs_json") val dayInputsJson: String,
    @SerialName("plan_json") val planJson: String? = null
)
