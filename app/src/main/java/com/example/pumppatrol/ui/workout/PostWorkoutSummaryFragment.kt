package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pumppatrol.R
import com.example.pumppatrol.databinding.FragmentPostWorkoutSummaryBinding
import com.example.pumppatrol.ui.home.HomeFragment
import androidx.navigation.fragment.findNavController


class PostWorkoutSummaryFragment : Fragment() {

    private var _binding: FragmentPostWorkoutSummaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PostWorkoutViewModel

    private var totalTime: Long = 0
    private var totalWater: Int = 0
    private var totalWeightLifted: Float = 0f
    private lateinit var workoutType: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostWorkoutSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[PostWorkoutViewModel::class.java]

        // Retrieve arguments
        arguments?.let {
            totalTime = it.getLong("totalTime")
            totalWater = it.getInt("totalWater")
            totalWeightLifted = it.getFloat("totalWeightLifted", 0f)
            workoutType = it.getString("workoutType", "Custom")
        }

        // Send data to ViewModel
        viewModel.setWorkoutData(workoutType, totalTime, totalWater, totalWeightLifted)

        // Observe LiveData
        viewModel.workoutType.observe(viewLifecycleOwner) {
            binding.textWorkoutType.text = it
        }

        viewModel.workoutSummary.observe(viewLifecycleOwner) {
            binding.textWorkoutTime.text = it
        }

        viewModel.totalWaterDrank.observe(viewLifecycleOwner) {
            binding.textWaterDrank.text = it
        }

        viewModel.totalWeightLifted.observe(viewLifecycleOwner) {
            binding.textTotalWeightLifted.text = it
        }

        viewModel.workoutIntensity.observe(viewLifecycleOwner) {
            binding.textWorkoutIntensity.text = it
        }

        // Finish button to go back home
        binding.btnFinish.setOnClickListener {
            goToHomeScreen()
        }
    }

    private fun goToHomeScreen() {
        findNavController().navigate(R.id.navigation_home)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
