package com.example.pumppatrol.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostWorkoutViewModel : ViewModel() {

    private val _workoutType = MutableLiveData<String>()
    val workoutType: LiveData<String> get() = _workoutType

    private val _workoutSummary = MutableLiveData<String>()
    val workoutSummary: LiveData<String> get() = _workoutSummary

    private val _totalWaterDrank = MutableLiveData<String>()
    val totalWaterDrank: LiveData<String> get() = _totalWaterDrank

    private val _totalWeightLifted = MutableLiveData<String>()
    val totalWeightLifted: LiveData<String> get() = _totalWeightLifted

    private val _workoutIntensity = MutableLiveData<String>()
    val workoutIntensity: LiveData<String> get() = _workoutIntensity

    fun setWorkoutData(workoutType: String, totalTime: Long, totalWater: Int, totalWeight: Float) {
        _workoutType.value = "🏋️‍♂️ Workout Type: $workoutType"

        val minutes = (totalTime / 60000).toInt()
        val seconds = (totalTime % 60000 / 1000).toInt()
        _workoutSummary.value = "⌚ Total Workout Time: ${String.format("%02d:%02d", minutes, seconds)}"

        _totalWaterDrank.value = "💧 Total Water Drank: $totalWater oz"
        _totalWeightLifted.value = "💪 Total Weight Lifted: ${"%.1f".format(totalWeight)} lbs"

        _workoutIntensity.value = when {
            totalWeight < 500 -> "🔥 Workout Intensity: Light"
            totalWeight < 1000 -> "🔥 Workout Intensity: Moderate"
            else -> "🔥 Workout Intensity: Heavy"
        }
    }
}
