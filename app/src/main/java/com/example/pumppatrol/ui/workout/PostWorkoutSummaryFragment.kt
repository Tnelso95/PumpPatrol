package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pumppatrol.R
import com.example.pumppatrol.databinding.FragmentPremadeWorkoutBinding
import com.example.pumppatrol.ui.home.HomeViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.pumppatrol.adapters.WorkoutAdapter
import com.example.pumppatrol.databinding.FragmentPostWorkoutSummaryBinding

class PostWorkoutSummaryFragment : Fragment() {

    private var _binding: FragmentPostWorkoutSummaryBinding? = null
    private val binding get() = _binding!!
    private val postWorkoutViewModel: PostWorkoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostWorkoutSummaryBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val btnFinish = view.findViewById<Button>(R.id.btnFinish)
//
//        btnFinish.setOnClickListener {
//            findNavController().navigate(R.id.action_postWorkoutSummaryFragment_to_navigation_home)
//        }
        binding.btnFinish.setOnClickListener {
            findNavController().navigate(R.id.action_postWorkoutSummaryFragment_to_navigation_workout)
        }

       // postWorkoutViewModel.workoutSummary.observe(viewLifecycleOwner) { summary ->
        //    binding.textSummary.text = summary

            //    binding.btnFinish.setOnClickListener {
          //      findNavController().navigate(R.id.navigation_home)
        //    }
      //  }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}