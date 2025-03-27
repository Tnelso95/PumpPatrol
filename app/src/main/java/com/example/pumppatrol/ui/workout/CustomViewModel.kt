package com.example.pumppatrol.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CustomViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the custom workout page"
    }

    val text: LiveData<String> = _text
}