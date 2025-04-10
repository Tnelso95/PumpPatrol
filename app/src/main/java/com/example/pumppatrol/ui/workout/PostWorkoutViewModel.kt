package com.example.pumppatrol.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostWorkoutViewModel : ViewModel() {

    private val _workoutSummary = MutableLiveData<String>()
    val workoutSummary: LiveData<String> get() = _workoutSummary

    private val _totalWaterDrank = MutableLiveData<Int>()
    val totalWaterDrank: LiveData<Int> get() = _totalWaterDrank

    fun setWorkoutData(exercises: List<String>, totalTime: Long, totalWater: Int) {
        _workoutSummary.value =
            "Workout Complete!\nExercises: ${exercises.joinToString(", ")}\nTotal Time: ${totalTime / 1000} seconds"
        _totalWaterDrank.value = totalWater
    }
}
