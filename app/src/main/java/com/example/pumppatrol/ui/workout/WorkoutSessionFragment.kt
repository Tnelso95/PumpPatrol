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

data class SetRecord(
    val setNumber: Int,
    val weight: Float,
    val reps: Int = 12
)

data class ExerciseRecord(
    val name: String,
    val sets: MutableList<SetRecord> = mutableListOf()
)

// ✅ Added workoutType
data class WorkoutRecord(
    val id: String,
    val title: String,
    val workoutType: String,
    val totalTime: Long,
    val date: String,
    val exercises: List<ExerciseRecord>
)

class WorkoutSessionFragment : Fragment() {

    private var _binding: FragmentWorkoutSessionBinding? = null
    private val binding get() = _binding!!

    private var exercises: List<String> = listOf()
    private var workoutType: String = "Custom" // Default value
    private var currentExerciseIndex = 0
    private var currentSetIndex = 1
    private val totalSetsPerExercise = 3

    private val exerciseRecords = mutableListOf<ExerciseRecord>()

    private var totalTime = 0L
    private var isRunning = false
    private val handler = Handler()

    private var hydrationGoal = 10
    private var sipsTaken = 0
    private val hydrationReminderInterval = 10 * 60 * 1000L
    private val hydrationPopupInterval = 5 * 60 * 1000L
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
            workoutType = it.getString("workout_type", workoutType) //  Get workout type
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

        binding.editTextWeight.text.clear()
        binding.editTextReps.text.clear()

        val currentExerciseRecord = exerciseRecords[currentExerciseIndex]
        val setRecord = SetRecord(setNumber = currentSetIndex, weight, reps)
        currentExerciseRecord.sets.add(setRecord)

        if (currentSetIndex < totalSetsPerExercise) {
            currentSetIndex++
            updateSetIndicator()
        } else {
            if (currentExerciseIndex < exercises.size - 1) {
                currentExerciseIndex++
                currentSetIndex = 1
                exerciseRecords.add(ExerciseRecord(name = exercises[currentExerciseIndex]))
                binding.textCurrentExercise.text = exercises[currentExerciseIndex]
                updateSetIndicator()
            } else {
                binding.textCurrentExercise.text = "Workout Complete!"
                isRunning = false
                handler.removeCallbacks(timerRunnable)
                saveWorkoutToFirebase()

                var totalWeightLifted = 0f
                for (exercise in exerciseRecords) {
                    for (set in exercise.sets) {
                        totalWeightLifted += set.weight * set.reps
                    }
                }

                val bundle = Bundle().apply {
                    putLong("totalTime", totalTime)
                    putInt("totalWater", sipsTaken)
                    putFloat("totalWeightLifted", totalWeightLifted)
                    putString("workoutType", workoutType) //  Pass to summary
                }
                findNavController().navigate(
                    R.id.action_workoutSessionFragment_to_postWorkoutSummaryFragment,
                    bundle
                )
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
            workoutType = workoutType, // Save type
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
                .setPositiveButton("Got it!") { dialog, _ -> dialog.dismiss() }
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
