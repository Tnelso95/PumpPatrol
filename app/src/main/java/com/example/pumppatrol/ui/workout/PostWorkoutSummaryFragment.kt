package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pumppatrol.databinding.FragmentPostWorkoutSummaryBinding

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
        viewModel = ViewModelProvider(requireActivity()).get(PostWorkoutViewModel::class.java)

        viewModel.workoutSummary.observe(viewLifecycleOwner) { summary ->
            binding.textWorkoutSummary.text = summary
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
