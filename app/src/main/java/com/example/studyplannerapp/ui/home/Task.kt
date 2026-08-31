package com.example.studyplannerapp.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val done: Boolean = false
)
