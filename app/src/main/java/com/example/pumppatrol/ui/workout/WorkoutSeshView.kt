package com.example.pumppatrol.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WorkoutSeshView : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the workout session page"
    }

    val text: LiveData<String> = _text
}