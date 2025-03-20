package com.example.pumppatrol.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WorkoutViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is where users will track their workouts"
    }
    val text: LiveData<String> = _text
}