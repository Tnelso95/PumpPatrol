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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pumppatrol.R
import com.example.pumppatrol.databinding.FragmentPremadeWorkoutBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PremadeWorkoutFragment : Fragment() {

    private var _binding: FragmentPremadeWorkoutBinding? = null
    private val binding get() = _binding!!

    // cache the latest snapshot so spinner changes can re-render
    private var latestSnapshot: DataSnapshot? = null

    // define your four splits
    private val dayOptions = listOf(
        "Chest & Triceps" to listOf("Chest", "Triceps"),
        "Back & Biceps"   to listOf("Back", "Biceps"),
        "Legs & Shoulders" to listOf("Legs", "Shoulders"),
        "Core"             to listOf("Core")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPremadeWorkoutBinding.inflate(inflater, container, false)
        val root = binding.root

        // 1) setup spinner
        val spinner = binding.spinnerDay
        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dayOptions.map { it.first }
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                latestSnapshot?.let { renderDay(it, dayOptions[pos].second) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // 2) load data once
        val dbRef = Firebase.database.getReference("Workouts")
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                latestSnapshot = snapshot
                // render the default first day
                renderDay(snapshot, dayOptions[spinner.selectedItemPosition].second)
            }
            override fun onCancelled(error: DatabaseError) {
                binding.textPremade.text = "Error: ${error.message}"
            }
        })

        // 3) start workout button
        binding.btnStartWorkout.setOnClickListener {
            // build exercise list from displayed groups
            val selectedGroups = dayOptions[binding.spinnerDay.selectedItemPosition].second
            val list = mutableListOf<String>()
            latestSnapshot?.let { snap ->
                selectedGroups.forEach { grp->
                    snap.child(grp).children
                        .mapNotNull { it.getValue(String::class.java) }
                        .take(3)
                        .also(list::addAll)
                }
            }
            val bundle = Bundle().apply {
                putStringArrayList("exercise_list", ArrayList(list))
            }
            findNavController().navigate(R.id.action_workoutOptions_to_workoutSessionFragment, bundle)
        }

        return root
    }

    private fun renderDay(snapshot: DataSnapshot, groups: List<String>) {
        val ssb = SpannableStringBuilder()
        groups.forEachIndexed { idx, group ->
            // Header
            val title = "$group:\n"
            val start = ssb.length
            ssb.append(title)
            ssb.setSpan(StyleSpan(Typeface.BOLD), start, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.setSpan(UnderlineSpan(), start, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Exercises
            val exList = snapshot.child(group).children
                .mapNotNull { it.getValue(String::class.java) }
                .take(if(group=="Core") 4 else 3) // 4 core, 3 others

            exList.forEach { ex ->
                val b = ssb.length
                ssb.append("  • $ex\n")
                ssb.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black)),
                    b, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // sets/reps stub
                val d = ssb.length
                ssb.append("     Sets: 3, Reps: 12\n")
                ssb.setSpan(RelativeSizeSpan(0.8f), d, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            if (idx < groups.lastIndex) ssb.append("\n")
        }

        binding.textPremade.text = ssb
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

