package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pumppatrol.databinding.FragmentWorkoutSessionBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.navigation.fragment.findNavController


class WorkoutSessionFragment : Fragment() {

    private var _binding: FragmentWorkoutSessionBinding? = null
    private val binding get() = _binding!!

    private var exercises: List<String> = listOf()
    private var currentExerciseIndex = 0

    private var totalTime = 0L // Total elapsed time in milliseconds
    private var isRunning = false
    private val handler = Handler()

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                totalTime += 1000 // Increase time by 1 second
                val minutes = (totalTime / 60000).toInt()
                val seconds = (totalTime % 60000 / 1000).toInt()
                binding.textWorkoutTimer.text = String.format("%02d:%02d", minutes, seconds)
                handler.postDelayed(this, 1000) // Run every second
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

        // Get the passed exercises from arguments
        arguments?.let {
            exercises = it.getStringArrayList("exercise_list") ?: listOf()
        }

        startWorkout()

        binding.btnNextExercise.setOnClickListener {
            nextExercise()
        }

        return root
    }

    private fun startWorkout() {
        if (exercises.isNotEmpty()) {
            binding.textCurrentExercise.text = exercises[currentExerciseIndex]
            startTimer()
        }
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            handler.post(timerRunnable) // Start counting time
        }
    }

    private fun nextExercise() {
        if (currentExerciseIndex < exercises.size - 1) {
            currentExerciseIndex++
            binding.textCurrentExercise.text = exercises[currentExerciseIndex]
        } else {
            binding.textCurrentExercise.text = "Workout Complete!"
            isRunning = false // Stop the timer
            saveWorkoutTime()


            val postWorkoutViewModel = ViewModelProvider(requireActivity()).get(PostWorkoutViewModel::class.java)
            postWorkoutViewModel.setWorkoutData(exercises, totalTime)

            // Navigate to Post-Workout Summary
            findNavController().navigate(R.id.action_workoutSessionFragment_to_postWorkoutSummaryFragment)

        }
    }

//    private fun saveWorkoutTime() {
//        val database = FirebaseDatabase.getInstance()
//        val myRef = database.getReference("WorkoutHistory")
//
//        // Save workout time under a unique ID
//        val workoutId = myRef.push().key
//        workoutId?.let {
//            myRef.child(it).setValue(totalTime)
//        }
//
//        //val myRef2 = database.getReference("WorkoutHistory")
//        // Save workout time under a unique ID
//        val workoutId2 = myRef.push().key
//        workoutId2?.let {
//            myRef.child(it).setValue(exercises)
//        }
//    }

    private fun saveWorkoutTime() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("CustomWorkoutHistory")

        // Create a readable timestamp for workout title
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val workoutTitle = "Workout_" + dateFormat.format(Date())

        // Structure data in a single entry
        val workoutData = mapOf(
            "title" to workoutTitle,
            "totalTime" to totalTime,
            "exercises" to exercises
        )

        // Save data under a single key
        myRef.child(workoutTitle).setValue(workoutData)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        isRunning = false
        handler.removeCallbacks(timerRunnable)
        _binding = null
    }
}



