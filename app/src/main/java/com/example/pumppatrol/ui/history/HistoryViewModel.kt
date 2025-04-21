package com.example.pumppatrol.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class HistoryViewModel : ViewModel() {

    private val _workoutHistory = MutableLiveData<String>()
    val workoutHistory: LiveData<String> = _workoutHistory

    init {
        fetchWorkoutHistory()
    }

    private fun fetchWorkoutHistory() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("WorkoutHistory")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val historyText = StringBuilder()

                // Collect the children into a List and reverse it
                val workouts = snapshot.children.toList().asReversed()

                for (workoutSnapshot in workouts) {
                    val title = workoutSnapshot.child("title").getValue(String::class.java)
                    val totalTime = workoutSnapshot.child("totalTime").getValue(Long::class.java)
                    if (title == null || totalTime == null) continue

                    historyText.append("Workout: ").append(title).append("\n")
                    historyText.append("  Duration: ").append(formatTime(totalTime)).append("\n")
                    historyText.append("  Exercises:\n")

                    val exercisesNode = workoutSnapshot.child("exercises")
                    for (exerciseChild in exercisesNode.children) {
                        val exerciseName = exerciseChild.child("name")
                            .getValue(String::class.java) ?: "Unnamed Exercise"
                        historyText.append("    - ").append(exerciseName).append("\n")

                        val setsNode = exerciseChild.child("sets")
                        for (setChild in setsNode.children) {
                            val setNumber = setChild.child("setNumber")
                                .getValue(Int::class.java) ?: -1
                            val weight = setChild.child("weight")
                                .getValue(Float::class.java) ?: 0f
                            val reps = setChild.child("reps")
                                .getValue(Int::class.java) ?: 0

                            historyText.append("         Set ")
                                .append(setNumber)
                                .append(": Weight: ")
                                .append(weight)
                                .append(", Reps: ")
                                .append(reps)
                                .append("\n")
                        }
                    }
                    historyText.append("\n")
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
        val seconds = ((milliseconds % 60000) / 1000).toInt()
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}
