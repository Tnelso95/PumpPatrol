package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pumppatrol.databinding.FragmentPremadeWorkoutBinding
import com.example.pumppatrol.ui.home.HomeViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PremadeWorkoutFragment : Fragment() {

    private var _binding: FragmentPremadeWorkoutBinding? = null
    private val binding get() = _binding!!

    //Declare database
    private val database = Firebase.database
    private val myRef = database.getReference("message")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val preViewModel =
            ViewModelProvider(this).get(PreViewModel::class.java)

        _binding = FragmentPremadeWorkoutBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //binding.textPremadeWorkout.text = "This is the premade"
        val textView: TextView = binding.textPremade
        preViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it


        }


        myRef.setValue("Hello, Firebase from PremadeWorkoutFragment!")

        // Read from the database
        myRef.get().addOnSuccessListener {
            textView.text = it.value.toString()
        }.addOnFailureListener{
            textView.text = "Error getting data"
        }

        preViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
