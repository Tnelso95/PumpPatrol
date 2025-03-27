package com.example.pumppatrol.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pumppatrol.databinding.FragmentCustomWorkoutBinding
import com.example.pumppatrol.ui.home.HomeViewModel

class CustomWorkoutFragment : Fragment() {

    private var _binding: FragmentCustomWorkoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val customViewModel =
            ViewModelProvider(this).get(CustomViewModel::class.java)

        _binding = FragmentCustomWorkoutBinding.inflate(inflater, container, false)
        val root: View = binding.root



        val textView: TextView = binding.textCustom
        customViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it


        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
