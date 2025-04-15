////package com.example.pumppatrol.ui.workout
////
////import android.R
////import android.os.Bundle
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.AdapterView
////import android.widget.ArrayAdapter
////import android.widget.TextView
////import android.widget.Toast
////import androidx.fragment.app.Fragment
////import androidx.lifecycle.ViewModelProvider
////import androidx.navigation.fragment.findNavController
////import com.example.pumppatrol.databinding.FragmentCustomWorkoutBinding
////import com.example.pumppatrol.ui.home.HomeViewModel
////import com.google.firebase.database.DataSnapshot
////import com.google.firebase.database.DatabaseError
////import com.google.firebase.database.ValueEventListener
////import com.google.firebase.database.ktx.database
////import com.google.firebase.ktx.Firebase
////
////class CustomWorkoutFragment : Fragment() {
////
////    private var _binding: FragmentCustomWorkoutBinding? = null
////    private val binding get() = _binding!!
////
////    private val database = Firebase.database
////    private val myRef = database.getReference("Workouts")
////
//////    private var selectedMuscle1: String? = null
//////    private var selectedMuscle2: String? = null
//////    private var selectedExercises = ArrayList<String>()
////
////    override fun onCreateView(
////        inflater: LayoutInflater,
////        container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View {
////        val customViewModel =
////            ViewModelProvider(this).get(CustomViewModel::class.java)
////
////        _binding = FragmentCustomWorkoutBinding.inflate(inflater, container, false)
////        val root: View = binding.root
////
////
////
////        // Get muscle groups from your dataset
////        val muscleGroups = WorkoutData.workouts.keys.toList()
////
////        // Create lists with a default prompt at the start
////        val bodyParts1 = mutableListOf("Select first muscle group")
////        bodyParts1.addAll(muscleGroups)
////        val bodyParts2 = mutableListOf("Select second muscle group")
////        bodyParts2.addAll(muscleGroups)
////
////        // Create adapters for the body part spinners
////        val bodyPartAdapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bodyParts1)
////        bodyPartAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////        val bodyPartAdapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bodyParts2)
////        bodyPartAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////
////        // Set the adapters for the muscle group spinners
////        binding.spinnerBodyPart1.adapter = bodyPartAdapter1
////        binding.spinnerBodyPart2.adapter = bodyPartAdapter2
////
////        // Set default adapters for the workout spinners
////        setDefaultWorkoutAdaptersFirst()
////        setDefaultWorkoutAdaptersSecond()
////
////        // When a muscle group is selected in the first spinner, update its workout spinners
////        binding.spinnerBodyPart1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
////            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
////                if (position == 0) {
////                    // Still at default; reset workouts to default text
////                    setDefaultWorkoutAdaptersFirst()
////                } else {
////                    // Adjust for the dummy prompt at index 0
////                    val selectedMuscle = muscleGroups[position - 1]
////                    val workouts = WorkoutData.getWorkoutsByCategory(selectedMuscle) ?: emptyList()
////
////                    // Build adapters that prepend the default title for each workout spinner
////                    val workoutList1 = mutableListOf("Workout 1")
////                    workoutList1.addAll(workouts)
////                    val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList1)
////                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////                    binding.spinnerWorkout1.adapter = adapter1
////
////                    val workoutList2 = mutableListOf("Workout 2")
////                    workoutList2.addAll(workouts)
////                    val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList2)
////                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////                    binding.spinnerWorkout2.adapter = adapter2
////
////                    val workoutList3 = mutableListOf("Workout 3")
////                    workoutList3.addAll(workouts)
////                    val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList3)
////                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////                    binding.spinnerWorkout3.adapter = adapter3
////                }
////            }
////            override fun onNothingSelected(parent: AdapterView<*>) {}
////        }
////
////        // When a muscle group is selected in the second spinner, update its workout spinners
////        binding.spinnerBodyPart2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
////            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
////                if (position == 0) {
////                    setDefaultWorkoutAdaptersSecond()
////                } else {
////                    val selectedMuscle = muscleGroups[position - 1]
////                    val workouts = WorkoutData.getWorkoutsByCategory(selectedMuscle) ?: emptyList()
////
////                    val workoutList4 = mutableListOf("Workout 1")
////                    workoutList4.addAll(workouts)
////                    val adapter4 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList4)
////                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////                    binding.spinnerWorkout4.adapter = adapter4
////
////                    val workoutList5 = mutableListOf("Workout 2")
////                    workoutList5.addAll(workouts)
////                    val adapter5 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList5)
////                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////                    binding.spinnerWorkout5.adapter = adapter5
////
////                    val workoutList6 = mutableListOf("Workout 3")
////                    workoutList6.addAll(workouts)
////                    val adapter6 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutList6)
////                    adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////                    binding.spinnerWorkout6.adapter = adapter6
////                }
////
////
////            }
////
////
////            override fun onNothingSelected(parent: AdapterView<*>) {}
////        }
////
////
////
////        binding.btnStartWorkout.setOnClickListener {
////            val selectedWorkouts = mutableListOf<String>()
////            var incomplete = false
////
//////            // Retrieve selected workouts if they are not default values
//////            listOf(
//////                binding.spinnerWorkout1, binding.spinnerWorkout2, binding.spinnerWorkout3,
//////                binding.spinnerWorkout4, binding.spinnerWorkout5, binding.spinnerWorkout6
//////            ).forEach { spinner ->
//////                val selectedItem = spinner.selectedItem.toString()
//////                if (!selectedItem.startsWith("Workout")) {  // Ignore default titles
//////                    selectedWorkouts.add(selectedItem)
//////                }
//////            }
////            listOf(
////                binding.spinnerWorkout1, binding.spinnerWorkout2, binding.spinnerWorkout3,
////                binding.spinnerWorkout4, binding.spinnerWorkout5, binding.spinnerWorkout6
////            ).forEach { spinner ->
////                val selectedItem = spinner.selectedItem.toString()
////                if (selectedItem.startsWith("Workout")) {  // Check for default titles
////                    incomplete = true
////                    return@forEach // Use return@forEach to continue to the next iteration
////                }
////                selectedWorkouts.add(selectedItem)
////            }
////
////            if (incomplete) {
////                Toast.makeText(requireContext(), "Custom workout not complete. Please select all workouts.", Toast.LENGTH_SHORT).show()
////            } else {
////                // Create a bundle and pass the selected workouts
////                val bundle = Bundle().apply {
////                    putStringArrayList("exercise_list", ArrayList(selectedWorkouts))
////                }
////
////                findNavController().navigate(
////                    com.example.pumppatrol.R.id.action_workoutOptions_to_workoutSessionFragment,
////                    bundle
////                )
////            }
////
////
//////            // Create a bundle and pass the selected workouts
//////            val bundle = Bundle().apply {
//////                putStringArrayList("exercise_list", ArrayList(selectedWorkouts))  // Use "exercise_list" to match PremadeWorkoutFragment
//////            }
//////
//////            findNavController().navigate(
//////                com.example.pumppatrol.R.id.action_workoutOptions_to_workoutSessionFragment,
//////                bundle
//////            )
////        }
////
////        val stringBuilder = StringBuilder()
////
////        // Ensure text is set properly
////        binding.textCustom.text = stringBuilder.toString()
////
////        fun onCancelled(error: DatabaseError) {
////            binding.textCustom.text = "Failed to read value: ${error.message}"
////        }
////
////
////
////        return root
////    }
////
////    // Helper function for default workout adapters for the first muscle group spinners
////    private fun setDefaultWorkoutAdaptersFirst() {
////        val defaultList1 = listOf("Workout 1")
////        val defaultList2 = listOf("Workout 2")
////        val defaultList3 = listOf("Workout 3")
////
////        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList1)
////        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////        binding.spinnerWorkout1.adapter = adapter1
////
////        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList2)
////        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////        binding.spinnerWorkout2.adapter = adapter2
////
////        val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList3)
////        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////        binding.spinnerWorkout3.adapter = adapter3
////    }
////
////    // Helper function for default workout adapters for the second muscle group spinners
////    private fun setDefaultWorkoutAdaptersSecond() {
////        val defaultList1 = listOf("Workout 1")
////        val defaultList2 = listOf("Workout 2")
////        val defaultList3 = listOf("Workout 3")
////
////        val adapter4 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList1)
////        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////        binding.spinnerWorkout4.adapter = adapter4
////
////        val adapter5 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList2)
////        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////        binding.spinnerWorkout5.adapter = adapter5
////
////        val adapter6 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, defaultList3)
////        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////        binding.spinnerWorkout6.adapter = adapter6
////    }
////
////
////    override fun onDestroyView() {
////        super.onDestroyView()
////        _binding = null
////    }
////}
//
//
//package com.example.pumppatrol.ui.workout
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.EditText
//import android.widget.Spinner
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.example.pumppatrol.R
//import com.example.pumppatrol.databinding.FragmentCustomWorkoutBinding
//
////data class CustomExercise(
////    var exercise: String,
////    var sets: Int
////)
////
////data class MuscleGroupSection(
////    var muscleGroup: String,
////    val exercises: MutableList<CustomExercise> = mutableListOf()
////)
//// Used by the custom workout configuration screen.
//data class CustomExercise(
//    val exercise: String,
//    val sets: Int
//)
//
//data class MuscleGroupSection(
//    val muscleGroup: String,
//    val exercises: List<CustomExercise>
//)
//
//// New model for a session item—each set to be performed.
//data class SessionItem(
//    val muscleGroup: String,
//    val exercise: String,
//    val setNumber: Int,
//    val totalSets: Int
//)
//
//// Optional: to hold the recorded result for a session item.
//data class SessionItemResult(
//    val item: SessionItem,
//    val weight: Float
//)
//
//
//
//class CustomWorkoutFragment : Fragment() {
//
//    private var _binding: FragmentCustomWorkoutBinding? = null
//    private val binding get() = _binding!!
//
//    // This list holds available muscle groups (keys from your WorkoutData)
//    // Replace with your own data source as needed.
//    private val muscleGroups = WorkoutData.workouts.keys.toList()
//
//    // When a muscle group is chosen, this function returns the list of available exercises.
//    private fun getExercisesForMuscle(muscleGroup: String): List<String> {
//        return WorkoutData.getWorkoutsByCategory(muscleGroup) ?: emptyList()
//    }
//
//    // Container to hold the custom workout configuration. Each entry represents a muscle group section.
//    private val muscleGroupSections = mutableListOf<MuscleGroupSection>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentCustomWorkoutBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        // When the fragment starts, add one muscle group section by default.
//        addMuscleGroupSection()
//
//        // Button to add a new muscle group section.
//        binding.btnAddMuscleGroup.setOnClickListener {
//            addMuscleGroupSection()
//        }
//
//        // When "Start Workout" is clicked, gather the configuration.
//        binding.btnStartWorkout.setOnClickListener {
//            val customWorkout = gatherCustomWorkout()
//            if (customWorkout.isEmpty()) {
//                Toast.makeText(requireContext(), "Please add at least one exercise", Toast.LENGTH_SHORT).show()
//            } else {
//                // For demonstration, we simply show a toast. You can pass the data in a bundle.
//                Toast.makeText(requireContext(), "Custom Workout: $customWorkout", Toast.LENGTH_LONG).show()
//
//                // For example, pack the workout data and navigate:
//                val bundle = Bundle().apply {
//                    // You may want to serialize the customWorkout list (or a JSON) and pass it along.
//                    putString("customWorkoutData", customWorkout.toString())
//                }
//                findNavController().navigate(R.id.action_workoutOptions_to_customWorkoutSessionFragment, bundle)
//            }
//        }
//
//        return root
//    }
//
//    // Function to inflate and add a new muscle group section.
//    private fun addMuscleGroupSection() {
//        val inflater = LayoutInflater.from(requireContext())
//        // Inflate the muscle group section layout.
//        val muscleGroupView = inflater.inflate(R.layout.layout_muscle_group_section, binding.containerMuscleGroups, false)
//        binding.containerMuscleGroups.addView(muscleGroupView)
//
//        // Set up the muscle group spinner.
//        val spinnerMuscleGroup = muscleGroupView.findViewById<Spinner>(R.id.spinnerMuscleGroup)
//        val muscleGroupList = mutableListOf("Select Muscle Group")
//        muscleGroupList.addAll(muscleGroups)
//        val muscleAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, muscleGroupList)
//        muscleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerMuscleGroup.adapter = muscleAdapter
//
//        // Container for exercise rows.
//        val containerExercises = muscleGroupView.findViewById<ViewGroup>(R.id.containerExercises)
//        // Button to add an exercise row within this muscle group section.
//        val btnAddExercise = muscleGroupView.findViewById<View>(R.id.btnAddExercise)
//        btnAddExercise.setOnClickListener {
//            addExerciseRow(containerExercises, spinnerMuscleGroup.selectedItemPosition)
//        }
//
//        // Add a default exercise row (if a muscle group is already selected, you may require user to choose first)
//        // Here we simply add a row so the user can modify it later.
//        addExerciseRow(containerExercises, spinnerMuscleGroup.selectedItemPosition)
//
//        // Optionally, listen to spinnerMuscleGroup changes to update already-added exercise rows.
//        spinnerMuscleGroup.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
//                // When muscle group is changed, update each exercise row spinner.
//                updateExerciseRowsForMuscle(containerExercises, position)
//            }
//            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
//        }
//    }
//
//    // Function to add an exercise row into a given container.
//    private fun addExerciseRow(container: ViewGroup, muscleGroupPosition: Int) {
//        val inflater = LayoutInflater.from(requireContext())
//        val exerciseRow = inflater.inflate(R.layout.layout_exercise_row, container, false)
//        container.addView(exerciseRow)
//
//        // Set the spinner adapter based on the selected muscle group.
//        val spinnerExercise = exerciseRow.findViewById<Spinner>(R.id.spinnerExercise)
//        if (muscleGroupPosition > 0) {
//            // muscleGroupPosition > 0 means a valid muscle group was selected (position 0 is the prompt).
//            val selectedMuscle = muscleGroups[muscleGroupPosition - 1]
//            val exercises = getExercisesForMuscle(selectedMuscle)
//            val exerciseAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, exercises)
//            exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinnerExercise.adapter = exerciseAdapter
//        } else {
//            // Provide an empty adapter (or a prompt) if not selected.
//            spinnerExercise.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Select Muscle Group First"))
//        }
//    }
//
//    // Update all exercise row spinners for the given muscle group.
//    private fun updateExerciseRowsForMuscle(container: ViewGroup, muscleGroupPosition: Int) {
//        val adapter: ArrayAdapter<String> = if (muscleGroupPosition > 0) {
//            val selectedMuscle = muscleGroups[muscleGroupPosition - 1]
//            val exercises = getExercisesForMuscle(selectedMuscle)
//            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, exercises)
//        } else {
//            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Select Muscle Group First"))
//        }
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        for (i in 0 until container.childCount) {
//            val exerciseRow = container.getChildAt(i)
//            val spinnerExercise = exerciseRow.findViewById<Spinner>(R.id.spinnerExercise)
//            spinnerExercise.adapter = adapter
//        }
//    }
//
//    // Gathers the configuration across all muscle group sections.
//    private fun gatherCustomWorkout(): List<MuscleGroupSection> {
//        val customWorkout = mutableListOf<MuscleGroupSection>()
//        // Loop through each muscle group section in the container.
//        for (i in 0 until binding.containerMuscleGroups.childCount) {
//            val muscleGroupView = binding.containerMuscleGroups.getChildAt(i)
//            val spinnerMuscleGroup = muscleGroupView.findViewById<Spinner>(R.id.spinnerMuscleGroup)
//            val selectedMusclePos = spinnerMuscleGroup.selectedItemPosition
//            if (selectedMusclePos <= 0) continue // skip if no muscle group is selected
//            val muscleGroupName = muscleGroups[selectedMusclePos - 1]
//
//            val containerExercises = muscleGroupView.findViewById<ViewGroup>(R.id.containerExercises)
//            val exercisesList = mutableListOf<CustomExercise>()
//            // For each exercise row, get the selected exercise and number of sets.
//            for (j in 0 until containerExercises.childCount) {
//                val exerciseRow = containerExercises.getChildAt(j)
//                val spinnerExercise = exerciseRow.findViewById<Spinner>(R.id.spinnerExercise)
//                val editTextSets = exerciseRow.findViewById<EditText>(R.id.editTextSets)
//                val exerciseName = spinnerExercise.selectedItem?.toString() ?: continue
//                val setsText = editTextSets.text.toString()
//                val sets = setsText.toIntOrNull() ?: 1 // default to 1 if nothing entered
//                exercisesList.add(CustomExercise(exerciseName, sets))
//            }
//            if (exercisesList.isNotEmpty()) {
//                customWorkout.add(MuscleGroupSection(muscleGroupName, exercisesList))
//            }
//        }
//        return customWorkout
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
//

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

// Data models used for custom workout configuration
data class CustomExercise(
    val exercise: String,
    val sets: Int
) : Serializable

data class MuscleGroupSection(
    val muscleGroup: String,
    val exercises: List<CustomExercise>
) : Serializable

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
            val customWorkoutConfig = gatherCustomWorkout()
            if (customWorkoutConfig.isEmpty()) {
                Toast.makeText(requireContext(), "Please add at least one exercise", Toast.LENGTH_SHORT).show()
            } else {
                // For simplicity, we serialize the configuration to a string.
                // In a production app, consider using a more robust method (e.g., Gson).
                val configString = customWorkoutConfig.toString()
                val bundle = Bundle().apply {
                    putString("customWorkoutData", configString)
                }
                findNavController().navigate(
                    R.id.action_workoutOptions_to_customWorkoutSessionFragment, bundle)
            }
        }
//// Suppose this function gathers your workout configuration.
//        fun gatherCustomWorkout(): List<MuscleGroupSection> {
//            val customWorkout = mutableListOf<MuscleGroupSection>()
//            // ... (your gathering logic here)
//            return customWorkout
//        }
//
//// In your btnStartWorkout click listener:
//        binding.btnStartWorkout.setOnClickListener {
//            val customWorkoutConfig = gatherCustomWorkout()
//            if (customWorkoutConfig.isEmpty()) {
//                Toast.makeText(requireContext(), "Please add at least one exercise", Toast.LENGTH_SHORT).show()
//            } else {
//                // Wrap the List in an ArrayList (which is Serializable) and pass it.
//                val bundle = Bundle().apply {
//                    putSerializable("customWorkoutData", ArrayList(customWorkoutConfig))
//                }
//                findNavController().navigate(R.id.action_workoutOptions_to_customWorkoutSessionFragment, bundle)
//            }
//        }

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

