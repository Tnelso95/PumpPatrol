package com.example.pumppatrol.ui.workout

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pumppatrol.databinding.FragmentCustomWorkoutBinding
import com.example.pumppatrol.ui.home.HomeViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CustomWorkoutFragment : Fragment() {

    private var _binding: FragmentCustomWorkoutBinding? = null
    private val binding get() = _binding!!

    private val database = Firebase.database
    private val myRef = database.getReference("Workouts")

//    private var selectedMuscle1: String? = null
//    private var selectedMuscle2: String? = null
//    private var selectedExercises = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val customViewModel =
            ViewModelProvider(this).get(CustomViewModel::class.java)

        _binding = FragmentCustomWorkoutBinding.inflate(inflater, container, false)
        val root: View = binding.root



        // Get muscle groups from your dataset
        val muscleGroups = WorkoutData.workouts.keys.toList()

        // Create lists with a default prompt at the start
        val bodyParts1 = mutableListOf("Select first muscle group")
        bodyParts1.addAll(muscleGroups)
        val bodyParts2 = mutableListOf("Select second muscle group")
        bodyParts2.addAll(muscleGroups)

        // Create adapters for the body part spinners
        val bodyPartAdapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bodyParts1)
        bodyPartAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val bodyPartAdapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bodyParts2)
        bodyPartAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapters for the muscle group spinners
        binding.spinnerBodyPart1.adapter = bodyPartAdapter1
        binding.spinnerBodyPart2.adapter = bodyPartAdapter2

        // Set default adapters for the workout spinners
        setDefaultWorkoutAdaptersFirst()
        setDefaultWorkoutAdaptersSecond()

        // When a muscle group is selected in the first spinner, update its workout spinners
        binding.spinnerBodyPart1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // Still at default; reset workouts to default text
                    setDefaultWorkoutAdaptersFirst()
                } else {
                    // Adjust for the dummy prompt at index 0
                    val selectedMuscle = muscleGroups[position - 1]
                    val workouts = WorkoutData.getWorkoutsByCategory(selectedMuscle) ?: emptyList()

                    // Build adapters that prepend the default title for each workout spinner
                    val workoutList1 = mutableListOf("Workout 1")
                    workoutList1.addAll(workouts)
                    val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList1)
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerWorkout1.adapter = adapter1

                    val workoutList2 = mutableListOf("Workout 2")
                    workoutList2.addAll(workouts)
                    val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList2)
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerWorkout2.adapter = adapter2

                    val workoutList3 = mutableListOf("Workout 3")
                    workoutList3.addAll(workouts)
                    val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList3)
                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerWorkout3.adapter = adapter3
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // When a muscle group is selected in the second spinner, update its workout spinners
        binding.spinnerBodyPart2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    setDefaultWorkoutAdaptersSecond()
                } else {
                    val selectedMuscle = muscleGroups[position - 1]
                    val workouts = WorkoutData.getWorkoutsByCategory(selectedMuscle) ?: emptyList()

                    val workoutList4 = mutableListOf("Workout 1")
                    workoutList4.addAll(workouts)
                    val adapter4 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList4)
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerWorkout4.adapter = adapter4

                    val workoutList5 = mutableListOf("Workout 2")
                    workoutList5.addAll(workouts)
                    val adapter5 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList5)
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerWorkout5.adapter = adapter5

                    val workoutList6 = mutableListOf("Workout 3")
                    workoutList6.addAll(workouts)
                    val adapter6 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList6)
                    adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerWorkout6.adapter = adapter6
                }


            }


            override fun onNothingSelected(parent: AdapterView<*>) {}
        }



        binding.btnStartWorkout.setOnClickListener {
            val selectedWorkouts = mutableListOf<String>()
            var incomplete = false

            val selectedMuscle1 = binding.spinnerBodyPart1.selectedItem.toString()
            val selectedMuscle2 = binding.spinnerBodyPart2.selectedItem.toString()

            val workoutType = when {
                selectedMuscle1.startsWith("Select") && selectedMuscle2.startsWith("Select") -> "Custom Workout"
                selectedMuscle1.startsWith("Select") -> selectedMuscle2
                selectedMuscle2.startsWith("Select") -> selectedMuscle1
                else -> "$selectedMuscle1 & $selectedMuscle2"
            }

//            // Retrieve selected workouts if they are not default values
//            listOf(
//                binding.spinnerWorkout1, binding.spinnerWorkout2, binding.spinnerWorkout3,
//                binding.spinnerWorkout4, binding.spinnerWorkout5, binding.spinnerWorkout6
//            ).forEach { spinner ->
//                val selectedItem = spinner.selectedItem.toString()
//                if (!selectedItem.startsWith("Workout")) {  // Ignore default titles
//                    selectedWorkouts.add(selectedItem)
//                }
//            }
            listOf(
                binding.spinnerWorkout1, binding.spinnerWorkout2, binding.spinnerWorkout3,
                binding.spinnerWorkout4, binding.spinnerWorkout5, binding.spinnerWorkout6
            ).forEach { spinner ->
                val selectedItem = spinner.selectedItem.toString()
                if (selectedItem.startsWith("Workout")) {  // Check for default titles
                    incomplete = true
                    return@forEach // Use return@forEach to continue to the next iteration
                }
                selectedWorkouts.add(selectedItem)
            }

            if (incomplete) {
                Toast.makeText(requireContext(), "Custom workout not complete. Please select all workouts.", Toast.LENGTH_SHORT).show()
            } else {
                // Create a bundle and pass the selected workouts
                val bundle = Bundle().apply {
                    putStringArrayList("exercise_list", ArrayList(selectedWorkouts))
                    putString("workout_type", workoutType)
                }

                findNavController().navigate(
                    com.example.pumppatrol.R.id.action_workoutOptions_to_workoutSessionFragment,
                    bundle
                )
            }


//            // Create a bundle and pass the selected workouts
//            val bundle = Bundle().apply {
//                putStringArrayList("exercise_list", ArrayList(selectedWorkouts))  // Use "exercise_list" to match PremadeWorkoutFragment
//            }
//
//            findNavController().navigate(
//                com.example.pumppatrol.R.id.action_workoutOptions_to_workoutSessionFragment,
//                bundle
//            )



        }




        val stringBuilder = StringBuilder()

        // Ensure text is set properly
        binding.textCustom.text = stringBuilder.toString()

        fun onCancelled(error: DatabaseError) {
            binding.textCustom.text = "Failed to read value: ${error.message}"
        }



        return root
    }

    // Helper function for default workout adapters for the first muscle group spinners
    private fun setDefaultWorkoutAdaptersFirst() {
        val defaultList1 = listOf("Workout 1")
        val defaultList2 = listOf("Workout 2")
        val defaultList3 = listOf("Workout 3")

        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList1)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWorkout1.adapter = adapter1

        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList2)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWorkout2.adapter = adapter2

        val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList3)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWorkout3.adapter = adapter3
    }

    // Helper function for default workout adapters for the second muscle group spinners
    private fun setDefaultWorkoutAdaptersSecond() {
        val defaultList1 = listOf("Workout 1")
        val defaultList2 = listOf("Workout 2")
        val defaultList3 = listOf("Workout 3")

        val adapter4 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList1)
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWorkout4.adapter = adapter4

        val adapter5 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList2)
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWorkout5.adapter = adapter5

        val adapter6 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList3)
        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWorkout6.adapter = adapter6
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
