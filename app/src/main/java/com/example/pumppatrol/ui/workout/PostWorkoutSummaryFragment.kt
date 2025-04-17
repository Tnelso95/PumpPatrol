package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pumppatrol.databinding.FragmentPostWorkoutSummaryBinding
import com.example.pumppatrol.R
import androidx.navigation.fragment.findNavController



class PostWorkoutSummaryFragment : Fragment() {

    private var _binding: FragmentPostWorkoutSummaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PostWorkoutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostWorkoutSummaryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[PostWorkoutViewModel::class.java]

        viewModel.workoutSummary.observe(viewLifecycleOwner) { summary ->
            // Set the workout time on the first CardView
            binding.textWorkoutTime.text = summary
        }

        viewModel.totalWaterDrank.observe(viewLifecycleOwner) { waterOz ->
            binding.textWaterDrank.text = "💧 Total Amount of Water Drank: ${waterOz} oz"
        }

        binding.btnFinish.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.navigation_home)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
