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
        val minutes = (totalTime / 60000).toInt()
        val seconds = (totalTime % 60000 / 1000).toInt()
        _workoutSummary.value = "🕒 Total Workout Time: ${String.format("%02d:%02d", minutes, seconds)}"
        _totalWaterDrank.value = totalWater
    }

}
