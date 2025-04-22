package com.example.pumppatrol.ui.history

data class WorkoutHistoryItem(
    val title: String,           // e.g. "2025-04-08 10:57"
    val durationMs: Long,
    val exercises: List<ExerciseRecord>
)

data class ExerciseRecord(
    val name: String,
    val sets: List<SetRecord>
)

data class SetRecord(
    val setNumber: Int,
    val weight: Float,
    val reps: Int
)
