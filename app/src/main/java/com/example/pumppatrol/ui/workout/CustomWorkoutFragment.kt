
package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pumppatrol.R
import com.example.pumppatrol.databinding.FragmentCustomWorkoutBinding
import java.io.Serializable


data class CustomExercise(var exercise: String, var sets: Int) : Serializable
data class MuscleGroupSection(var muscleGroup: String, val exercises: MutableList<CustomExercise>) : Serializable


class CustomWorkoutFragment : Fragment() {

    private var _binding: FragmentCustomWorkoutBinding? = null
    private val binding get() = _binding!!

    // Replace with your actual data source for muscle groups.
    private val muscleGroups = WorkoutData.workouts.keys.toList()

    // Returns a list of exercises for a given muscle group.
    private fun getExercisesForMuscle(muscleGroup: String): List<String> {
        return WorkoutData.getWorkoutsByCategory(muscleGroup) ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomWorkoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Add one muscle group section by default.
        addMuscleGroupSection()

        // Button to add additional muscle group sections.
        binding.btnAddMuscleGroup.setOnClickListener {
            addMuscleGroupSection()
        }

        // When "Start Workout" is clicked, gather configuration and navigate.
        binding.btnStartWorkout.setOnClickListener {
            val customWorkout = gatherCustomWorkout()
            if (customWorkout.isEmpty()) {
                Toast.makeText(context, "Please add at least one exercise", Toast.LENGTH_SHORT).show()
            } else {
                // Prepare bundle with custom workout data
                val bundle = Bundle()
                bundle.putSerializable("customWorkoutData", ArrayList(customWorkout))
                findNavController().navigate(
                    R.id.action_customWorkoutFragment_to_customWorkoutSessionFragment,
                    bundle
                )
            }
        }


        return root
    }

    // Dynamically inflate and add a new muscle group section.
    private fun addMuscleGroupSection() {
        val inflater = LayoutInflater.from(requireContext())
        val muscleGroupView = inflater.inflate(R.layout.layout_muscle_group_section, binding.containerMuscleGroups, false)
        binding.containerMuscleGroups.addView(muscleGroupView)

        // Set up the muscle group spinner.
        val spinnerMuscleGroup = muscleGroupView.findViewById<Spinner>(R.id.spinnerMuscleGroup)
        val muscleGroupList = mutableListOf("Select Muscle Group")
        muscleGroupList.addAll(muscleGroups)
        val muscleAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, muscleGroupList)
        muscleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMuscleGroup.adapter = muscleAdapter

        // Container for exercise rows.
        val containerExercises = muscleGroupView.findViewById<ViewGroup>(R.id.containerExercises)
        // Button to add an exercise row.
        val btnAddExercise = muscleGroupView.findViewById<View>(R.id.btnAddExercise)
        btnAddExercise.setOnClickListener {
            addExerciseRow(containerExercises, spinnerMuscleGroup.selectedItemPosition)
        }

        // Add one default exercise row.
        addExerciseRow(containerExercises, spinnerMuscleGroup.selectedItemPosition)

        // Update exercise spinners if the muscle group selection changes.
        spinnerMuscleGroup.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                updateExerciseRowsForMuscle(containerExercises, position)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    // Inflate an exercise row inside a given container.
    private fun addExerciseRow(container: ViewGroup, muscleGroupPosition: Int) {
        val inflater = LayoutInflater.from(requireContext())
        val exerciseRow = inflater.inflate(R.layout.layout_exercise_row, container, false)
        container.addView(exerciseRow)
        val spinnerExercise = exerciseRow.findViewById<Spinner>(R.id.spinnerExercise)
        if (muscleGroupPosition > 0) {
            val selectedMuscle = muscleGroups[muscleGroupPosition - 1]
            val exercises = getExercisesForMuscle(selectedMuscle)
            val exerciseAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, exercises)
            exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerExercise.adapter = exerciseAdapter
        } else {
            spinnerExercise.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Select Muscle Group First"))
        }
    }

    // Update each exercise row spinner when the muscle group changes.
    private fun updateExerciseRowsForMuscle(container: ViewGroup, muscleGroupPosition: Int) {
        val adapter: ArrayAdapter<String> = if (muscleGroupPosition > 0) {
            val selectedMuscle = muscleGroups[muscleGroupPosition - 1]
            val exercises = getExercisesForMuscle(selectedMuscle)
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, exercises)
        } else {
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Select Muscle Group First"))
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        for (i in 0 until container.childCount) {
            val exerciseRow = container.getChildAt(i)
            val spinnerExercise = exerciseRow.findViewById<Spinner>(R.id.spinnerExercise)
            spinnerExercise.adapter = adapter
        }
    }

    // Gathers the custom workout configuration.
    private fun gatherCustomWorkout(): List<MuscleGroupSection> {
        val customWorkout = mutableListOf<MuscleGroupSection>()
        for (i in 0 until binding.containerMuscleGroups.childCount) {
            val muscleGroupView = binding.containerMuscleGroups.getChildAt(i)
            val spinnerMuscleGroup = muscleGroupView.findViewById<Spinner>(R.id.spinnerMuscleGroup)
            val selectedMusclePos = spinnerMuscleGroup.selectedItemPosition
            if (selectedMusclePos <= 0) continue
            val muscleGroupName = muscleGroups[selectedMusclePos - 1]

            val containerExercises = muscleGroupView.findViewById<ViewGroup>(R.id.containerExercises)
            val exercisesList = mutableListOf<CustomExercise>()
            for (j in 0 until containerExercises.childCount) {
                val exerciseRow = containerExercises.getChildAt(j)
                val spinnerExercise = exerciseRow.findViewById<Spinner>(R.id.spinnerExercise)
                val editTextSets = exerciseRow.findViewById<EditText>(R.id.editTextSets)
                val exerciseName = spinnerExercise.selectedItem?.toString() ?: continue
                val setsText = editTextSets.text.toString()
                val sets = setsText.toIntOrNull() ?: 1
                exercisesList.add(CustomExercise(exerciseName, sets))
            }
            if (exercisesList.isNotEmpty()) {
                customWorkout.add(MuscleGroupSection(muscleGroupName, exercisesList))
            }
        }
        return customWorkout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

