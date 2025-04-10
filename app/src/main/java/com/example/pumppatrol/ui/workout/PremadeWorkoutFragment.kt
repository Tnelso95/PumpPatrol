package com.example.pumppatrol.ui.workout

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

class PremadeWorkoutFragment : Fragment() {

    private var _binding: FragmentPremadeWorkoutBinding? = null
    private val binding get() = _binding!!

    //Declare database
    private val database = Firebase.database
    private val myRef = database.getReference("Workouts")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
//
    ): View {
        _binding = FragmentPremadeWorkoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Ensure we use binding for UI elements
        val textView: TextView = binding.textPremade
        val btnCustom = binding.btnStartWorkout

        val bundle = Bundle()

//        btnCustom.setOnClickListener {
//            findNavController().navigate(R.id.action_workoutOptions_to_workoutSessionFragment)
//        }



        // Firebase Listener
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                val stringBuilder = StringBuilder()
////                val chestWorkouts = snapshot.child("Chest").children.mapNotNull { it.getValue(String::class.java) }.take(3)
////                val tricepsWorkouts = snapshot.child("Triceps").children.mapNotNull { it.getValue(String::class.java) }.take(3)
//
//                val bundle = Bundle()
//                val chestWorkouts = snapshot.child("Chest").children.mapNotNull { it.getValue(String::class.java) }.take(3)
//                val tricepsWorkouts = snapshot.child("Triceps").children.mapNotNull { it.getValue(String::class.java) }.take(3)
//
//                val exerciseList = ArrayList(chestWorkouts + tricepsWorkouts)
//                bundle.putStringArrayList("exercise_list", exerciseList)
//
//                //findNavController().navigate(R.id.action_workoutOptions_to_workoutSessionFragment, bundle)
//
//
//
//                stringBuilder.append("Chest and Triceps Day:\n\n")
//
//                stringBuilder.append("Chest:\n")
//                chestWorkouts.forEach { stringBuilder.append("  - $it\n") }
//
//                stringBuilder.append("\nTriceps:\n")
//                tricepsWorkouts.forEach { stringBuilder.append("  - $it\n") }
//
//                // Ensure text is set properly
//                binding.textPremade.text = stringBuilder.toString()
//                // Move navigation to button click
//                binding.btnStartWorkout.setOnClickListener {
//                    findNavController().navigate(R.id.action_workoutOptions_to_workoutSessionFragment, bundle)
//                }
//
//            }

                val ssb = SpannableStringBuilder()

                // 1. Append Header: "Chest and Triceps Day:" -> Bold & Underlined
                val header = "Chest and Triceps Day:\n\n"
                ssb.append(header)
                ssb.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    header.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                ssb.setSpan(
                    UnderlineSpan(),
                    0,
                    header.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // 2. Append Chest Section Header with a “strong color”
                val chestTitle = "Chest:\n"
                val chestTitleStart = ssb.length
                ssb.append(chestTitle)
                ssb.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(requireContext(), R.color.black)
                    ),
                    chestTitleStart,
                    ssb.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Retrieve chest workouts (change as needed if sets and reps data are available)
                val chestWorkouts = snapshot.child("Chest").children
                    .mapNotNull { it.getValue(String::class.java) }
                    .take(3)

                // Loop through chest workouts and append each exercise and its dummy sets/reps info
                for (exercise in chestWorkouts) {
                    // Append exercise name with a bullet and an indentation, using a “lighter color”
                    val exerciseText = "  • $exercise\n"
                    val exerciseStart = ssb.length
                    ssb.append(exerciseText)
                    ssb.setSpan(

                        ForegroundColorSpan(
                            ContextCompat.getColor(requireContext(), androidx.cardview.R.color.cardview_dark_background)
                        ),
                        exerciseStart,
                        ssb.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    // Append dummy sets/reps data (modify as needed with actual info from your database)
                    val detailText = "     Sets: 3, Reps: 12\n"
                    val detailStart = ssb.length
                    ssb.append(detailText)
                    // Slightly reduce font size for details
                    ssb.setSpan(
                        RelativeSizeSpan(0.8f),
                        detailStart,
                        ssb.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                // 3. Append Triceps Section Header with a “strong color”
                val tricepsTitle = "\nTriceps:\n"
                val tricepsTitleStart = ssb.length
                ssb.append(tricepsTitle)
                ssb.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(requireContext(), R.color.black)
                    ),
                    tricepsTitleStart,
                    ssb.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Retrieve triceps workouts
                val tricepsWorkouts = snapshot.child("Triceps").children
                    .mapNotNull { it.getValue(String::class.java) }
                    .take(3)

                // Loop through triceps workouts and add details
                for (exercise in tricepsWorkouts) {
                    val exerciseText = "  • $exercise\n"
                    val exerciseStart = ssb.length
                    ssb.append(exerciseText)
                    ssb.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(requireContext(), androidx.cardview.R.color.cardview_dark_background)
                        ),
                        exerciseStart,
                        ssb.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    val detailText = "     Sets: 3, Reps: 12\n"
                    val detailStart = ssb.length
                    ssb.append(detailText)
                    ssb.setSpan(
                        RelativeSizeSpan(0.8f),
                        detailStart,
                        ssb.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                // Set the styled text to your TextView
                binding.textPremade.text = ssb

                // Bundle exercise list if needed for navigation (using your current approach)
                val bundle = Bundle()
                val exerciseList = ArrayList(chestWorkouts + tricepsWorkouts)
                bundle.putStringArrayList("exercise_list", exerciseList)

                // Start workout on button click
                binding.btnStartWorkout.setOnClickListener {
                    findNavController().navigate(R.id.action_workoutOptions_to_workoutSessionFragment, bundle)
                }
            }



            override fun onCancelled(error: DatabaseError) {
                binding.textPremade.text = "Failed to read value: ${error.message}"
            }
        })



        return root
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
