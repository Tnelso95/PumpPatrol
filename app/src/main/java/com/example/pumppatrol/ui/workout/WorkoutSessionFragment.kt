package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pumppatrol.R
import com.example.pumppatrol.databinding.FragmentWorkoutSessionBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutSessionFragment : Fragment() {

    private var _binding: FragmentWorkoutSessionBinding? = null
    private val binding get() = _binding!!

    private var exercises: List<String> = listOf()
    private var currentExerciseIndex = 0

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
                binding.textWorkoutTimer.text = String.format("%02d:%02d", minutes, seconds)

                // Hydration logic
                hydrationGoal = hydrationBlocks * 10
                val currentProgress = minOf(sipsTaken * 1, hydrationGoal)
                binding.textHydrationReminder.text = "Hydration: $currentProgress / $hydrationGoal oz"
                binding.progressHydration.max = hydrationGoal
                binding.progressHydration.progress = currentProgress

                if (totalTime % hydrationReminderInterval == 0L && totalTime > 0) {
                    binding.textHydrationReminder.text = "💧 Time to drink 10 oz of water!"
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
        val preViewModel = ViewModelProvider(this).get(WorkoutSeshView::class.java)
        _binding = FragmentWorkoutSessionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        arguments?.let {
            exercises = it.getStringArrayList("exercise_list") ?: listOf()
        }

        startWorkout()

        binding.btnNextExercise.setOnClickListener {
            nextExercise()
        }

        binding.btnSipWater.setOnClickListener {
            sipsTaken++
            val progress = minOf(sipsTaken, hydrationGoal)
            binding.textHydrationReminder.text = "Hydration: $progress / $hydrationGoal oz"
            binding.progressHydration.progress = progress
        }

        return root
    }

    private fun startWorkout() {
        if (exercises.isNotEmpty()) {
            binding.textCurrentExercise.text = exercises[currentExerciseIndex]
            updateExerciseCounter()
            startTimer()
        }
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            handler.post(timerRunnable)
        }
    }

    private fun updateExerciseCounter() {
        val currentExerciseNumber = currentExerciseIndex + 1
        val totalExercises = exercises.size
        binding.textExerciseProgress.text = "Exercise $currentExerciseNumber/$totalExercises"
    }

    private fun nextExercise() {
        if (currentExerciseIndex < exercises.size - 1) {
            currentExerciseIndex++
            binding.textCurrentExercise.text = exercises[currentExerciseIndex]
            updateExerciseCounter()
        } else {
            binding.textCurrentExercise.text = "Workout Complete!"
            isRunning = false
            saveWorkoutTime()

            val postWorkoutViewModel = ViewModelProvider(requireActivity()).get(PostWorkoutViewModel::class.java)
            postWorkoutViewModel.setWorkoutData(exercises, totalTime)

            findNavController().navigate(R.id.action_workoutSessionFragment_to_postWorkoutSummaryFragment)
        }
    }

    private fun saveWorkoutTime() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("CustomWorkoutHistory")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val workoutTitle = "Workout_" + dateFormat.format(Date())

        val workoutData = mapOf(
            "title" to workoutTitle,
            "totalTime" to totalTime,
            "exercises" to exercises
        )

        myRef.child(workoutTitle).setValue(workoutData)
    }

    private fun showHydrationPopup() {
        activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it)
                .setTitle("Hydration Reminder 💧")
                .setMessage("Here is a friendly reminder to keep drinking water!")
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
