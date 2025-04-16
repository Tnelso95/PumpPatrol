
package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pumppatrol.R
import com.example.pumppatrol.databinding.FragmentWorkoutSessionBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class to hold one set's record
data class SetRecord(
    val setNumber: Int,
    val weight: Float,
    val reps: Int = 12  // default to 12 reps
)

// Data class to hold each exercise's records
data class ExerciseRecord(
    val name: String,
    val sets: MutableList<SetRecord> = mutableListOf()
)

// Data class to hold the entire workout record
data class WorkoutRecord(
    val id: String,
    val title: String,
    val totalTime: Long,
    val date: String,
    val exercises: List<ExerciseRecord>
)

class WorkoutSessionFragment : Fragment() {

    private var _binding: FragmentWorkoutSessionBinding? = null
    private val binding get() = _binding!!

    // Exercises passed from previous fragment (e.g. from PremadeWorkoutFragment)
    private var exercises: List<String> = listOf()
    private var currentExerciseIndex = 0
    private var currentSetIndex = 1
    private val totalSetsPerExercise = 3  // You can adjust if needed

    // List to store records for each exercise
    private val exerciseRecords = mutableListOf<ExerciseRecord>()

    // Timer variables
    private var totalTime = 0L  // Total elapsed time in milliseconds
    private var isRunning = false
    private val handler = Handler()

    // Timer runnable to count elapsed time
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                totalTime += 1000  // Increase time by 1 second
                val minutes = (totalTime / 60000).toInt()
                val seconds = (totalTime % 60000 / 1000).toInt()
                binding.textWorkoutTimer.text = String.format("%02d:%02d", minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutSessionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Retrieve exercise names passed from the previous fragment
        arguments?.let {
            exercises = it.getStringArrayList("exercise_list") ?: listOf()
        }

        // Start with the first exercise if available
        if (exercises.isNotEmpty()) {
            exerciseRecords.add(ExerciseRecord(name = exercises[currentExerciseIndex]))
            binding.textCurrentExercise.text = exercises[currentExerciseIndex]
            updateSetIndicator()
            startTimer()
        }

        // Button to record the weight for the current set
        binding.btnAddWeight.setOnClickListener {
            addWeightForSet()
        }

        return root
    }

    // Starts the workout timer
    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            handler.post(timerRunnable)
        }
    }

    // Updates the UI indicator for current set progress
    private fun updateSetIndicator() {
        binding.textExerciseProgress.text = "Set $currentSetIndex / $totalSetsPerExercise"
    }

    // Called when the user clicks the Add Weight button
    private fun addWeightForSet() {
        val weightInput = binding.editTextWeight.text.toString()
        if (weightInput.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter weight", Toast.LENGTH_SHORT).show()
            return
        }

        val weight = weightInput.toFloatOrNull()
        if (weight == null) {
            Toast.makeText(requireContext(), "Invalid weight value", Toast.LENGTH_SHORT).show()
            return
        }

        val repsInput = binding.editTextReps.text.toString()
        if (repsInput.isBlank()) {
            Toast.makeText(requireContext(), "Please enter reps", Toast.LENGTH_SHORT).show()
            return
        }

        val reps = repsInput.toIntOrNull()
        if (reps == null) {
            Toast.makeText(requireContext(), "Invalid reps value", Toast.LENGTH_SHORT).show()
            return
        }

        // Clear the input field after retrieving its value
        binding.editTextWeight.text.clear()
        binding.editTextReps.text.clear()
        // Record the current set
        val currentExerciseRecord = exerciseRecords[currentExerciseIndex]
        val setRecord = SetRecord(setNumber = currentSetIndex, weight, reps)
        currentExerciseRecord.sets.add(setRecord)

        if (currentSetIndex < totalSetsPerExercise) {
            currentSetIndex++
            updateSetIndicator()
        } else {
            //Toast.makeText(requireContext(), "Completed ${exercises[currentExerciseIndex]}", Toast.LENGTH_SHORT).show()

            // Move to the next exercise if there is one
            if (currentExerciseIndex < exercises.size - 1) {
                currentExerciseIndex++
                currentSetIndex = 1
                exerciseRecords.add(ExerciseRecord(name = exercises[currentExerciseIndex]))
                binding.textCurrentExercise.text = exercises[currentExerciseIndex]
                updateSetIndicator()
            } else {
                // If all exercises are done, finish workout
                binding.textCurrentExercise.text = "Workout Complete!"
                isRunning = false
                handler.removeCallbacks(timerRunnable)
                saveWorkoutToFirebase()
                findNavController().navigate(R.id.action_workoutSessionFragment_to_postWorkoutSummaryFragment)
            }
        }
    }

    // Saves the complete workout to Firebase using the new structured format
    private fun saveWorkoutToFirebase() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("WorkoutHistory")

        // Create a unique workout title (this will also serve as the workout ID)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val workoutTitle = "Workout_" + dateFormat.format(Date())

        // Use an ISO8601 date string (or another preferred format)
        val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val dateStr = isoDateFormat.format(Date())

        val workoutRecord = WorkoutRecord(
            id = workoutTitle,
            title = workoutTitle,
            totalTime = totalTime,
            date = dateStr,
            exercises = exerciseRecords
        )

        // Save the workout record under a unique key
        myRef.child(workoutTitle).setValue(workoutRecord)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isRunning = false
        handler.removeCallbacks(timerRunnable)
        _binding = null
    }
}


