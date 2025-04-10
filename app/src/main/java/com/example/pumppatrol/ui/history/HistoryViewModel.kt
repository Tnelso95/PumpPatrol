
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

                for (workoutSnapshot in snapshot.children) {
                    // Retrieve the main fields from each workout
                    val title = workoutSnapshot.child("title").getValue(String::class.java)
                    val totalTime = workoutSnapshot.child("totalTime").getValue(Long::class.java)

                    // Skip if either is missing
                    if (title == null || totalTime == null) continue

                    // Append workout header info
                    historyText.append("Workout: ").append(title).append("\n")
                    historyText.append("  Duration: ").append(formatTime(totalTime)).append("\n")
                    historyText.append("  Exercises:\n")

                    // Get the node that holds all exercises
                    val exercisesNode = workoutSnapshot.child("exercises")

                    // Iterate over each exercise (e.g. "0", "1", "2" keys)
                    for (exerciseChild in exercisesNode.children) {
                        // Retrieve the exercise name
                        val exerciseName = exerciseChild.child("name")
                            .getValue(String::class.java) ?: "Unnamed Exercise"
                        historyText.append("    - ").append(exerciseName).append("\n")

                        // Retrieve the sets node
                        val setsNode = exerciseChild.child("sets")
                        // Iterate over each set
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
                    historyText.append("\n") // Spacing between workouts
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



