package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
                val stringBuilder = StringBuilder()
//                val chestWorkouts = snapshot.child("Chest").children.mapNotNull { it.getValue(String::class.java) }.take(3)
//                val tricepsWorkouts = snapshot.child("Triceps").children.mapNotNull { it.getValue(String::class.java) }.take(3)

                val bundle = Bundle()
                val chestWorkouts = snapshot.child("Chest").children.mapNotNull { it.getValue(String::class.java) }.take(3)
                val tricepsWorkouts = snapshot.child("Triceps").children.mapNotNull { it.getValue(String::class.java) }.take(3)

                val exerciseList = ArrayList(chestWorkouts + tricepsWorkouts)
                bundle.putStringArrayList("exercise_list", exerciseList)

                //findNavController().navigate(R.id.action_workoutOptions_to_workoutSessionFragment, bundle)



                stringBuilder.append("Chest and Triceps Day:\n\n")

                stringBuilder.append("Chest:\n")
                chestWorkouts.forEach { stringBuilder.append("  - $it\n") }

                stringBuilder.append("\nTriceps:\n")
                tricepsWorkouts.forEach { stringBuilder.append("  - $it\n") }

                // Ensure text is set properly
                binding.textPremade.text = stringBuilder.toString()
                // Move navigation to button click
                binding.btnStartWorkout.setOnClickListener {
                    findNavController().navigate(R.id.action_workoutOptions_to_workoutSessionFragment, bundle)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                binding.textPremade.text = "Failed to read value: ${error.message}"
            }
        })


//        btnCustom.setOnClickListener {
//            findNavController().navigate(R.id.action_workoutOptions_to_workoutSessionFragment, bundle)
//        }

        return root
    }


//        val textView: TextView = binding.textPremade
//        val stringBuilder = StringBuilder()
//
//        for ((category, exercises) in WorkoutData.workouts) {
//            stringBuilder.append("$category:\n")
//            exercises.forEach { exercise ->
//                stringBuilder.append("  - $exercise\n")
//            }
//        }
//
//        textView.text = stringBuilder.toString()


//        //binding.textPremadeWorkout.text = "This is the premade"
//        val textView: TextView = binding.textPremade
//        preViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//
//
//        }
//
//
//        myRef.setValue("Back")
//
//
//        // Read from the database
//        myRef.get().addOnSuccessListener {
//            textView.text = it.value.toString()
//        }.addOnFailureListener{
//            textView.text = "Error getting data"
//        }
//
//        preViewModel.text.observe(viewLifecycleOwner) {
//            //textView.text = it
//        }

//                val textView: TextView = binding.textPremade
//        preViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//
//
//        }
//
//        // Read from the database
//        myRef.get().addOnSuccessListener {
//            textView.text = it.value.toString()
//        }.addOnFailureListener{
//            textView.text = "Error getting data"
//        }
//
//        preViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
//
//        // Read from the database
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val value = snapshot.getValue(String::class.java)
//                textView.text = "Value from Firebase: $value"
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                textView.text = "Failed to read value: ${error.message}"
//            }
//        })

        //val textView: TextView = binding.textPremade

        // Read from the database
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//
//                // Iterate over the children of "Workouts" (Chest, Back, Shoulders, etc.)
//                val stringBuilder = StringBuilder()
//                for (workoutTypeSnapshot in snapshot.children) {
//                    val workoutTypeName = workoutTypeSnapshot.key // e.g., "Chest", "Back"
//                    stringBuilder.append("$workoutTypeName:\n")
//
//                    // Now get the list of exercises for this workout type
//                    if (workoutTypeSnapshot.value is List<*>) {
//                        val exercises = workoutTypeSnapshot.getValue(List::class.java) as List<String>
//                        for (exercise in exercises) {
//                            stringBuilder.append("  - $exercise\n")
//                        }
//                    }
//                }
//                textView.text = stringBuilder.toString()
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                textView.text = "Failed to read value: ${error.message}"
//            }
//        })




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
