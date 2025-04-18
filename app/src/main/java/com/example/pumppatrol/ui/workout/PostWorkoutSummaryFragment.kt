package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pumppatrol.databinding.FragmentPostWorkoutSummaryBinding
import com.example.pumppatrol.ui.workout.ExerciseRecord
import com.example.pumppatrol.ui.workout.SetRecord

class PostWorkoutSummaryFragment : Fragment() {

    private var _binding: FragmentPostWorkoutSummaryBinding? = null
    private val binding get() = _binding!!

    private var totalTime: Long = 0
    private var totalWater: Int = 0
    private lateinit var exerciseRecords: ArrayList<ExerciseRecord>

    private var totalWeightLifted: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostWorkoutSummaryBinding.inflate(inflater, container, false)

        arguments?.let {
            totalTime = it.getLong("totalTime")
            totalWater = it.getInt("totalWater")
            totalWeightLifted = it.getFloat("totalWeightLifted", 0f)
        }

        displayWorkoutSummary()
        return binding.root
    }

    private fun displayWorkoutSummary() {
        val minutes = (totalTime / 60000).toInt()
        val seconds = (totalTime % 60000 / 1000).toInt()
        binding.textWorkoutTime.text = "⌚Total Workout Time: $minutes:$seconds"
        binding.textWaterDrank.text = "💧Total Water Drank: $totalWater oz"
        binding.textTotalWeightLifted.text = "💪Total Weight Lifted: ${"%.1f".format(totalWeightLifted)} lbs"

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
