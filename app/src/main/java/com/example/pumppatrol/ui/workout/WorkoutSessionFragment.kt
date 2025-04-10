//package com.example.pumppatrol.ui.workout
//
//import android.os.Bundle
//import android.os.Handler
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
////import androidx.compose.ui.semantics.text
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import androidx.navigation.fragment.findNavController
//import com.example.pumppatrol.databinding.FragmentWorkoutSessionBinding
//import com.google.firebase.database.FirebaseDatabase
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//import androidx.navigation.fragment.findNavController
//import com.example.pumppatrol.R
//
//
//
//class WorkoutSessionFragment : Fragment() {
//
//    private var _binding: FragmentWorkoutSessionBinding? = null
//    private val binding get() = _binding!!
//
//    private var exercises: List<String> = listOf()
//    private var currentExerciseIndex = 0
//
//    private var totalTime = 0L // Total elapsed time in milliseconds
//    private var isRunning = false
//    private val handler = Handler()
//
//    private val timerRunnable = object : Runnable {
//        override fun run() {
//            if (isRunning) {
//                totalTime += 1000 // Increase time by 1 second
//                val minutes = (totalTime / 60000).toInt()
//                val seconds = (totalTime % 60000 / 1000).toInt()
//                binding.textWorkoutTimer.text = String.format("%02d:%02d", minutes, seconds)
//                handler.postDelayed(this, 1000) // Run every second
//            }
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val preViewModel = ViewModelProvider(this).get(WorkoutSeshView::class.java)
//
//        _binding = FragmentWorkoutSessionBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        // Get the passed exercises from arguments
//        arguments?.let {
//            exercises = it.getStringArrayList("exercise_list") ?: listOf()
//        }
//
//        startWorkout()
//
//        binding.btnNextExercise.setOnClickListener {
//            nextExercise()
//        }
//
//        return root
//    }
//
//    private fun startWorkout() {
//        if (exercises.isNotEmpty()) {
//            binding.textCurrentExercise.text = exercises[currentExerciseIndex]
//            startTimer()
//        }
//    }
//
//    private fun startTimer() {
//        if (!isRunning) {
//            isRunning = true
//            handler.post(timerRunnable) // Start counting time
//        }
//    }
//
//    private fun updateExerciseCounter() {
//        val currentExerciseNumber = currentExerciseIndex + 1
//        val totalExercises = exercises.size
//        binding.textExerciseProgress.text = "$currentExerciseNumber/$totalExercises"
//    }
//
//    private fun nextExercise() {
//        if (currentExerciseIndex < exercises.size - 1) {
//            currentExerciseIndex++
//            binding.textCurrentExercise.text = exercises[currentExerciseIndex]
//            updateExerciseCounter()
//        } else {
//            binding.textCurrentExercise.text = "Workout Complete!"
//            isRunning = false // Stop the timer
//            saveWorkoutTime()
//
//
//            val postWorkoutViewModel = ViewModelProvider(requireActivity()).get(PostWorkoutViewModel::class.java)
//            postWorkoutViewModel.setWorkoutData(exercises, totalTime)
//
//            // Navigate to Post-Workout Summary
//            findNavController().navigate(R.id.action_workoutSessionFragment_to_postWorkoutSummaryFragment)
//
//        }
//    }
//
////    private fun saveWorkoutTime() {
////        val database = FirebaseDatabase.getInstance()
////        val myRef = database.getReference("WorkoutHistory")
////
////        // Save workout time under a unique ID
////        val workoutId = myRef.push().key
////        workoutId?.let {
////            myRef.child(it).setValue(totalTime)
////        }
////
////        //val myRef2 = database.getReference("WorkoutHistory")
////        // Save workout time under a unique ID
////        val workoutId2 = myRef.push().key
////        workoutId2?.let {
////            myRef.child(it).setValue(exercises)
////        }
////    }
//
//    private fun saveWorkoutTime() {
//        val database = FirebaseDatabase.getInstance()
//        val myRef = database.getReference("CustomWorkoutHistory")
//
//        // Create a readable timestamp for workout title
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
//        val workoutTitle = "Workout_" + dateFormat.format(Date())
//
//        // Structure data in a single entry
//        val workoutData = mapOf(
//            "title" to workoutTitle,
//            "totalTime" to totalTime,
//            "exercises" to exercises
//        )
//
//        // Save data under a single key
//        myRef.child(workoutTitle).setValue(workoutData)
//    }
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        isRunning = false
//        handler.removeCallbacks(timerRunnable)
//        _binding = null
//    }
//}
//
//
//


package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
    val reps: Int = 12  // default to 12 reps (adjust as needed)
)

// Data class to hold each exercise's records (name and all its sets)
data class ExerciseRecord(
    val name: String,
    val sets: MutableList<SetRecord> = mutableListOf()
)

class WorkoutSessionFragment : Fragment() {

    private var _binding: FragmentWorkoutSessionBinding? = null
    private val binding get() = _binding!!

    // Exercises passed from previous fragment (e.g., from PremadeWorkoutFragment)
    private var exercises: List<String> = listOf()
    private var currentExerciseIndex = 0
    private var currentSetIndex = 1
    private val totalSetsPerExercise = 3  // you can update this if you allow for different set counts

    // To store records for each exercise
    private val workoutRecords = mutableListOf<ExerciseRecord>()

    // Timer variables
    private var totalTime = 0L  // milliseconds elapsed during workout
    private var isRunning = false
    private val handler = Handler()

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

        // Retrieve exercise names (passed from the previous fragment)
        arguments?.let {
            exercises = it.getStringArrayList("exercise_list") ?: listOf()
        }

        // If there are exercises, initialize with the first one.
        if (exercises.isNotEmpty()) {
            workoutRecords.add(ExerciseRecord(name = exercises[currentExerciseIndex]))
            binding.textCurrentExercise.text = exercises[currentExerciseIndex]
            updateSetIndicator()
            startTimer()
        }

        // New button listener: record the weight for the current set.
        binding.btnAddWeight.setOnClickListener {
            addWeightForSet()
        }

        return root
    }

    // Start the workout timer.
    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            handler.post(timerRunnable)
        }
    }

    // Update a TextView that shows the set progress ("Set x/3").
    private fun updateSetIndicator() {
        binding.textExerciseProgress.text = "Set $currentSetIndex / $totalSetsPerExercise"
    }

    // Called when the user clicks the Add Weight button.
    private fun addWeightForSet() {
        // Get numerical input from the EditText (note: the inputType is set to numberDecimal in the layout)
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

        // Clear the weight input field after reading its value.
        binding.editTextWeight.text.clear()

        // Record the set with its weight and default reps value.
        val currentExerciseRecord = workoutRecords[currentExerciseIndex]
        val setRecord = SetRecord(setNumber = currentSetIndex, weight = weight)
        currentExerciseRecord.sets.add(setRecord)

        // If we have not reached the total sets count for the current exercise, increment the set counter.
        if (currentSetIndex < totalSetsPerExercise) {
            currentSetIndex++
            updateSetIndicator()
        } else {
            // All sets for this exercise have been recorded
            Toast.makeText(requireContext(),
                "Completed ${exercises[currentExerciseIndex]}",
                Toast.LENGTH_SHORT).show()

            // Move to the next exercise if available.
            if (currentExerciseIndex < exercises.size - 1) {
                currentExerciseIndex++
                currentSetIndex = 1
                workoutRecords.add(ExerciseRecord(name = exercises[currentExerciseIndex]))
                binding.textCurrentExercise.text = exercises[currentExerciseIndex]
                updateSetIndicator()
            } else {
                // No more exercises: workout is complete.
                binding.textCurrentExercise.text = "Workout Complete!"
                isRunning = false
                handler.removeCallbacks(timerRunnable)
                saveWorkoutToFirebase()
                // Navigate to the Post-Workout Summary screen.
                findNavController().navigate(R.id.action_workoutSessionFragment_to_postWorkoutSummaryFragment)
            }
        }
    }

    // Save the complete workout session data (with sets and weights) into Firebase.
    private fun saveWorkoutToFirebase() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("WorkoutHistory")

        // Create a unique workout title based on the timestamp.
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val workoutTitle = "Workout_" + dateFormat.format(Date())

        // Prepare a list of maps for each exercise.
        val exercisesData = workoutRecords.map { exerciseRecord ->
            val setsData = exerciseRecord.sets.map { setRecord ->
                mapOf(
                    "setNumber" to setRecord.setNumber,
                    "weight" to setRecord.weight,
                    "reps" to setRecord.reps
                )
            }
            mapOf(
                "name" to exerciseRecord.name,
                "sets" to setsData
            )
        }

        // Create the whole workout data map to send to Firebase.
        val workoutData = mapOf(
            "title" to workoutTitle,
            "totalTime" to totalTime,
            "exercises" to exercisesData
        )

        // Save the workout data under a unique key.
        myRef.child(workoutTitle).setValue(workoutData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isRunning = false
        handler.removeCallbacks(timerRunnable)
        _binding = null
    }
}

