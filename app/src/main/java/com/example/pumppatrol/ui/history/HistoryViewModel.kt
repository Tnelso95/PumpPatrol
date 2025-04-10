
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

//package com.example.pumppatrol.ui.history
//
//import android.graphics.Typeface
//import android.text.Spannable
//import android.text.SpannableStringBuilder
//import android.text.style.StyleSpan
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.GenericTypeIndicator
//import com.google.firebase.database.ValueEventListener
//import java.util.Locale
//
//class HistoryViewModel : ViewModel() {
//
//    private val _workoutHistory = MutableLiveData<CharSequence>()
//    val workoutHistory: LiveData<CharSequence> = _workoutHistory
//
//    init {
//        fetchWorkoutHistory()
//    }
//
//    private fun fetchWorkoutHistory() {
//        val database = FirebaseDatabase.getInstance()
//        // Updated path from "CustomWorkoutHistory" to "WorkoutHistory"
//        val myRef = database.getReference("WorkoutHistory")
//
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val historyText = SpannableStringBuilder()
//                for (workoutSnapshot in snapshot.children) {
//                    val title = workoutSnapshot.child("title").getValue(String::class.java)
//                    val totalTime = workoutSnapshot.child("totalTime").getValue(Long::class.java)
//                    // Retrieve the list of exercises as a list of maps.
//                    val exercisesIndicator = object : GenericTypeIndicator<List<Map<String, Any>>>() {}
//                    val exercisesGeneric = workoutSnapshot.child("exercises").getValue(exercisesIndicator)
//
//                    if (title != null && totalTime != null && exercisesGeneric != null) {
//                        // Append Workout Title (bold)
//                        val headerText = "Workout: $title\n"
//                        val startHeader = historyText.length
//                        historyText.append(headerText)
//                        historyText.setSpan(
//                            StyleSpan(Typeface.BOLD),
//                            startHeader,
//                            historyText.length,
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                        )
//
//                        // Append duration information
//                        historyText.append("  Duration: ${formatTime(totalTime)}\n")
//                        historyText.append("  Exercises:\n")
//
//                        // List each exercise and its set details
//                        for (exerciseMap in exercisesGeneric) {
//                            // Get exercise name from the map
//                            val exerciseName = exerciseMap["name"] as? String ?: "Unnamed Exercise"
//                            historyText.append("    • $exerciseName\n")
//
//                            // Get the set details (list of maps)
//                            val sets = exerciseMap["sets"] as? List<Map<String, Any>>
//                            if (sets != null) {
//                                for (setMap in sets) {
//                                    val setNumber = setMap["setNumber"]?.toString() ?: "?"
//                                    val weight = setMap["weight"]?.toString() ?: "?"
//                                    val reps = setMap["reps"]?.toString() ?: "?"
//                                    historyText.append("         Set $setNumber: Weight: $weight, Reps: $reps\n")
//                                }
//                            }
//                        }
//                        // Add a separator line between workouts
//                        historyText.append("\n---------------------\n\n")
//                    }
//                }
//                _workoutHistory.value = historyText
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                _workoutHistory.value = "Error fetching workout history: ${error.message}"
//            }
//        })
//    }
//
//    private fun formatTime(milliseconds: Long): String {
//        val minutes = (milliseconds / 60000).toInt()
//        val seconds = (milliseconds % 60000 / 1000).toInt()
//        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
//    }
//}


