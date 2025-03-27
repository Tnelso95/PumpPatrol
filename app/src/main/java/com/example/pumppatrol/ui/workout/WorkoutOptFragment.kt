package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pumppatrol.R

class WorkoutOptionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment (make sure the XML file name is fragment_workout_options.xml)
        val view = inflater.inflate(R.layout.fragment_workout, container, false)

        // Get the buttons from the layout
        val btnPremade = view.findViewById<Button>(R.id.btn_premade_workout)
        val btnCustom = view.findViewById<Button>(R.id.btn_custom_workout)

        // Set click listeners to navigate to the respective fragments
        btnPremade.setOnClickListener {
            findNavController().navigate(R.id.action_workoutOptions_to_premadeWorkoutFragment)
        }

        btnCustom.setOnClickListener {
            findNavController().navigate(R.id.action_workoutOptions_to_customWorkoutFragment)
        }

        return view
    }
}
