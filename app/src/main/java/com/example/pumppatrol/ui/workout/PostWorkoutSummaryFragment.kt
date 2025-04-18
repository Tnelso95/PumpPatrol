package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pumppatrol.databinding.FragmentPostWorkoutSummaryBinding
import com.example.pumppatrol.ui.home.HomeFragment
import com.example.pumppatrol.R


class PostWorkoutSummaryFragment : Fragment() {

    private var _binding: FragmentPostWorkoutSummaryBinding? = null
    private val binding get() = _binding!!

    private var totalTime: Long = 0
    private var totalWater: Int = 0
    private var totalWeightLifted: Float = 0f
    private lateinit var workoutType: String

    // This method replaces the current fragment with the home screen fragment
    private fun goToHomeScreen() {
        // Replace this with the fragment you want to navigate to (e.g., HomeFragment)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, HomeFragment())  // R.id.fragment_container is the container of your fragments
        transaction.addToBackStack(null)  // Optional: if you want to add this transaction to the back stack
        transaction.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostWorkoutSummaryBinding.inflate(inflater, container, false)

        // Retrieve data passed in the arguments
        arguments?.let {
            totalTime = it.getLong("totalTime")
            totalWater = it.getInt("totalWater")
            totalWeightLifted = it.getFloat("totalWeightLifted", 0f)
            workoutType = it.getString("workoutType", "Custom")
        }

        // Display the workout summary
        displayWorkoutSummary()

        // Set up Finish button to go to home screen
        binding.btnFinish.setOnClickListener {
            goToHomeScreen()  // Replace with navigation logic
        }

        return binding.root
    }

    private fun displayWorkoutSummary() {
        // Format time
        val minutes = (totalTime / 60000).toInt()
        val seconds = (totalTime % 60000 / 1000).toInt()
        val formattedTime = String.format("%02d:%02d", minutes, seconds)

        // Determine workout intensity
        val intensity = when {
            totalWeightLifted < 500 -> "Light"
            totalWeightLifted < 1000 -> "Moderate"
            else -> "Heavy"
        }

        // Display values
        binding.textWorkoutType.text = "🏋️‍♂️ Workout Type: $workoutType"
        binding.textWorkoutTime.text = "⌚ Total Workout Time: $formattedTime"
        binding.textWaterDrank.text = "💧 Total Water Drank: $totalWater oz"
        binding.textTotalWeightLifted.text = "💪 Total Weight Lifted: ${"%.1f".format(totalWeightLifted)} lbs"
        binding.textWorkoutIntensity.text = "🔥 Workout Intensity: $intensity"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
