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
import com.example.pumppatrol.databinding.FragmentCustomWorkoutSessionBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

data class SessionItem(
    val muscleGroup: String,
    val exercise: String,
    val setNumber: Int,
    val totalSets: Int
) : java.io.Serializable

data class SessionItemResult(
    val item: SessionItem,
    val weight: Float,
    val reps: Int
) : java.io.Serializable

class CustomWorkoutSessionFragment : Fragment() {

    data class SetRecord(
        val setNumber: Int,
        val weight: Float,
        val reps: Int = 12
    )

    data class ExerciseRecord(
        val name: String,
        val sets: MutableList<SetRecord> = mutableListOf()
    )

    data class WorkoutRecord(
        val id: String,
        val title: String,
        val totalTime: Long,
        val date: String,
        val exercises: List<ExerciseRecord>
    )

    private var _binding: FragmentCustomWorkoutSessionBinding? = null
    private val binding get() = _binding!!

    // Timer variables
    private var totalTime = 0L
    private var isRunning = false
    private val handler = Handler()

    // Hydration tracking variables
    private var hydrationGoal = 10
    private var sipsTaken = 0
    private val hydrationReminderInterval = 10 * 60 * 1000L
    private val hydrationPopupInterval = 5 * 60 * 1000L
    private var lastPopupTime = 0L

    // Session items and results for the custom workout.
    private val sessionItems = mutableListOf<SessionItem>()
    private var currentSessionIndex = 0
    private val sessionResults = mutableListOf<SessionItemResult>()

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                totalTime += 1000
                val minutes = (totalTime / 60000).toInt()
                val seconds = ((totalTime % 60000) / 1000).toInt()
                binding.textWorkoutTimer.text = String.format("%02d:%02d", minutes, seconds)

                // Hydration reminder logic
                if ((totalTime - lastPopupTime) >= hydrationPopupInterval) {
                    lastPopupTime = totalTime
                    showHydrationPopup()
                }

                val hydrationBlocks = (totalTime / hydrationReminderInterval).toInt() + 1
                hydrationGoal = hydrationBlocks * 10

                val currentProgress = min(sipsTaken, hydrationGoal)
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomWorkoutSessionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        @Suppress("UNCHECKED_CAST")
        val customWorkoutConfig = arguments?.getSerializable("customWorkoutData") as? ArrayList<MuscleGroupSection>
        if (customWorkoutConfig.isNullOrEmpty()) {
            Toast.makeText(context, "No workout data found.", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return root
        }

        // Build the sessionItems list.
        sessionItems.clear()
        for (section in customWorkoutConfig) {
            for (exercise in section.exercises) {
                val totalSets = exercise.sets
                for (setNum in 1..totalSets) {
                    sessionItems.add(
                        SessionItem(
                            muscleGroup = section.muscleGroup,
                            exercise = exercise.exercise,
                            setNumber = setNum,
                            totalSets = totalSets
                        )
                    )
                }
            }
        }

        if (sessionItems.isEmpty()) {
            Toast.makeText(context, "Custom workout data is empty.", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return root
        }

        displayCurrentSessionItem()
        startTimer()

        binding.btnAddWeight.setOnClickListener {
            addWeightForSessionItem()
        }

        binding.btnSipWater.setOnClickListener {
            sipsTaken++
            val progress = min(sipsTaken, hydrationGoal)
            binding.textHydrationReminder.text = "Hydration: $progress / $hydrationGoal oz"
            binding.progressHydration.progress = progress
        }

        return root
    }

    private fun displayCurrentSessionItem() {
        if (currentSessionIndex < sessionItems.size) {
            val item = sessionItems[currentSessionIndex]
            binding.textCurrentExercise.text = "${item.muscleGroup}: ${item.exercise}"
            binding.textExerciseProgress.text = "Set ${item.setNumber} of ${item.totalSets}"
        }
    }

    private fun addWeightForSessionItem() {
        val weightInput = binding.editTextWeight.text.toString()
        if (weightInput.isBlank()) {
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
        val currentItem = sessionItems[currentSessionIndex]
        sessionResults.add(SessionItemResult(currentItem, weight, reps))
        currentSessionIndex++
        if (currentSessionIndex < sessionItems.size) {
            displayCurrentSessionItem()
        } else {
            binding.textCurrentExercise.text = "Workout Complete!"
            isRunning = false
            handler.removeCallbacks(timerRunnable)
            saveWorkoutToFirebaseCustom()

            // New additions start here:
            val totalWeightLifted = sessionResults.sumOf { (it.weight * it.reps).toDouble() }.toFloat()
            val workoutType = sessionItems.map { it.muscleGroup }
                .distinct()
                .joinToString(", ")

            val bundle = Bundle().apply {
                putLong("totalTime", totalTime)
                putInt("totalWater", sipsTaken)
                putFloat("totalWeightLifted", totalWeightLifted)
                putString("workoutType", workoutType)
            }

            findNavController().navigate(
                R.id.action_customWorkoutSessionFragment_to_postWorkoutSummaryFragment,
                bundle
            )
        }
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            handler.post(timerRunnable)
        }
    }

    private fun saveWorkoutToFirebaseCustom() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("WorkoutHistory")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val workoutTitle = "Workout_" + dateFormat.format(Date())
        val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val dateStr = isoDateFormat.format(Date())

        // Here, group sessionResults back into ExerciseRecords.
        val exerciseRecords = mutableListOf<ExerciseRecord>()
        val grouped = sessionItems.groupBy { Pair(it.muscleGroup, it.exercise) }
        for ((key, items) in grouped) {
            val record = ExerciseRecord(name = key.second)
            for (item in items) {
                val result = sessionResults.firstOrNull { it.item == item }
                val reps = result?.reps ?: 0
                val weight = result?.weight ?: 0f
                record.sets.add(SetRecord(item.setNumber, weight, reps))
            }
            exerciseRecords.add(record)
        }

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
                .setPositiveButton("Got it!") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        //This is a test to make sure this page is pushed to main
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isRunning = false
        handler.removeCallbacks(timerRunnable)
        _binding = null
    }
}
