package com.example.pumppatrol.ui.workout

object WorkoutData {
    val workouts = mapOf(
        "Chest" to listOf("Bench Press", "Incline Bench Press", "Decline Bench Press", "Dumbbell Flyes"),
        "Back" to listOf("Pull-Ups", "Lat Pulldown", "Bent-Over Rows", "Deadlifts"),
        "Shoulders" to listOf("Overhead Press", "Dumbbell Shoulder Press", "Arnold Press", "Lateral Raises"),
        "Biceps" to listOf("Barbell Curl", "Dumbbell Curl", "Hammer Curl", "Concentration Curl"),
        "Triceps" to listOf("Close-Grip Bench Press", "Tricep Dips", "Skull Crushers", "Overhead Triceps Extension"),
        "Legs" to listOf("Squats", "Front Squats", "Bulgarian Split Squats", "Leg Press"),
        "Core" to listOf("Plank", "Hanging Leg Raises", "Russian Twists", "Bicycle Crunches")
    )

    fun getWorkoutsByCategory(category: String): List<String>? {
        return workouts[category]
    }
}
