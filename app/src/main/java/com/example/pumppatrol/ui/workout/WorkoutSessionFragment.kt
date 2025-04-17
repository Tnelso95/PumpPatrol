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

    private var exercises: List<String> = listOf()
    private var currentExerciseIndex = 0
    private var currentSetIndex = 1
    private val totalSetsPerExercise = 3  // You can adjust if needed

    // List to store records for each exercise
    private val exerciseRecords = mutableListOf<ExerciseRecord>()

    // Timer variables
    private var totalTime = 0L
    private var isRunning = false
    private val handler = Handler()

    // Hydration tracking
    private var hydrationGoal = 10
    private var sipsTaken = 0
    private val hydrationReminderInterval = 10 * 60 * 1000L // 10 minutes in ms
    private val hydrationPopupInterval = 5 * 60 * 1000L // 5 minutes
    private var lastPopupTime = 0L

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                totalTime += 1000
                if ((totalTime - lastPopupTime) >= hydrationPopupInterval) {
                    lastPopupTime = totalTime
                    showHydrationPopup()
                }

                val minutes = (totalTime / 60000).toInt()
                val seconds = (totalTime % 60000 / 1000).toInt()
                val hydrationBlocks = (totalTime / hydrationReminderInterval).toInt() + 1
                hydrationGoal = hydrationBlocks * 10

                binding.textWorkoutTimer.text = String.format("%02d:%02d", minutes, seconds)

                val currentProgress = minOf(sipsTaken * 1, hydrationGoal)
                binding.textHydrationReminder.text = "Hydration: $currentProgress / $hydrationGoal oz"
                binding.progressHydration.max = hydrationGoal
                binding.progressHydration.progress = currentProgress

                if (totalTime % hydrationReminderInterval == 0L && totalTime > 0) {
                    binding.textHydrationReminder.text = "Time to drink 10 oz of water!"
                }

                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutSessionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        arguments?.let {
            exercises = it.getStringArrayList("exercise_list") ?: listOf()
        }


        binding.btnSipWater.setOnClickListener {
            sipsTaken++
            val progress = minOf(sipsTaken, hydrationGoal)
            binding.textHydrationReminder.text = "Hydration: $progress / $hydrationGoal oz"
            binding.progressHydration.progress = progress
        }
        if (exercises.isNotEmpty()) {
            exerciseRecords.add(ExerciseRecord(name = exercises[currentExerciseIndex]))
            binding.textCurrentExercise.text = exercises[currentExerciseIndex]
            updateSetIndicator()
            startTimer()
        }
        binding.btnAddWeight.setOnClickListener {
            addWeightForSet()
        }
        return root
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            handler.post(timerRunnable)
        }
    }

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
                val totalWaterDrank = sipsTaken // 1 sip = 1 oz
                handler.removeCallbacks(timerRunnable)
                saveWorkoutToFirebase()
                findNavController().navigate(R.id.action_workoutSessionFragment_to_postWorkoutSummaryFragment)
            }
        }
    }

    private fun saveWorkoutToFirebase() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("WorkoutHistory")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val workoutTitle = "Workout_" + dateFormat.format(Date())

        val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val dateStr = isoDateFormat.format(Date())

        val workoutRecord = WorkoutRecord(
            id = workoutTitle,
            title = workoutTitle,
            totalTime = totalTime,
            date = dateStr,
            exercises = exerciseRecords
        )

        myRef.child(workoutTitle).setValue(workoutRecord)
    }

    private fun showHydrationPopup() {
        activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it)
                .setTitle("Hydration Reminder 💧")
                .setMessage("Here is a friendly reminder to keep drinking water! You should take 10 sips (10 ounces) every 10 minutes!")
                .setPositiveButton("Got it!") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isRunning = false
        handler.removeCallbacks(timerRunnable)
        _binding = null
    }
}
