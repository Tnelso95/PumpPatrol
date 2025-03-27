package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pumppatrol.R

class WorkoutFragment : Fragment() {

    private lateinit var textWorkout: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private val handler = Handler(Looper.getMainLooper())
    private var seconds = 0
    private var running = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_workout, container, false)

        // Get the buttons by their IDs
        val btnPremade = view.findViewById<Button>(R.id.btn_premade_workout)
        val btnCustom = view.findViewById<Button>(R.id.btn_custom_workout)

        // Set up click listeners to navigate to the respective fragments
        btnPremade.setOnClickListener {
            findNavController().navigate(R.id.action_workoutOptions_to_premadeWorkoutFragment)
        }

        btnCustom.setOnClickListener {
            findNavController().navigate(R.id.action_workoutOptions_to_customWorkoutFragment)
        }

        return view
    }
}

        //textWorkout = view.findViewById(R.id.text_workout)
//        btnStart = view.findViewById(R.id.btn_start)
//        btnStop = view.findViewById(R.id.btn_stop)
//        btnStart = view.findViewById(R.id.btn_premade_workout)
//        btnStop = view.findViewById(R.id.btn_custom_workout)
//
//        btnStart.setOnClickListener {
//            running = true
//            handler.post(runnable)
//        }
//
//        btnStop.setOnClickListener {
//            running = false
//            handler.removeCallbacks(runnable)
//        }
//
//        return view
//    }
//
//    private val runnable = object : Runnable {
//        override fun run() {
//            if (running) {
//                seconds++
//                val minutes = seconds / 60
//                val secs = seconds % 60
//                textWorkout.text = String.format("%02d:%02d", minutes, secs)
//                handler.postDelayed(this, 1000)
//            }
//        }
//    }
//}
