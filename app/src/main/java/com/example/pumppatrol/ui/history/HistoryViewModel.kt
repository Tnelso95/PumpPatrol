
package com.example.pumppatrol.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryViewModel : ViewModel() {

    private val _workoutHistory = MutableLiveData<String>()
    val workoutHistory: LiveData<String> = _workoutHistory

    init {
        fetchWorkoutHistory()
    }

    private fun fetchWorkoutHistory() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("CustomWorkoutHistory") // Use the correct path

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val historyText = StringBuilder()
                for (workoutSnapshot in snapshot.children) {
                    val title = workoutSnapshot.child("title").getValue(String::class.java)
                    val totalTime = workoutSnapshot.child("totalTime").getValue(Long::class.java)
                    val exercises = workoutSnapshot.child("exercises").getValue(object : com.google.firebase.database.GenericTypeIndicator<List<String>>() {})

                    if (title != null && totalTime != null && exercises != null) {
                        historyText.append("Workout: ").append(title).append("\n")
                        historyText.append("  Duration: ").append(formatTime(totalTime)).append("\n")
                        historyText.append("  Exercises:\n")
                        for (exercise in exercises) {
                            historyText.append("    - ").append(exercise).append("\n")
                        }
                        historyText.append("\n") // Add a newline for spacing between workouts
                    }
                }
                _workoutHistory.value = historyText.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                _workoutHistory.value = "Error fetching workout history: ${error.message}"
            }
        })
    }

    private fun formatTime(milliseconds: Long): String {
        val minutes = (milliseconds / 60000).toInt()
        val seconds = (milliseconds % 60000 / 1000).toInt()
        return String.format("%02d:%02d", minutes, seconds)
    }
}