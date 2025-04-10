package com.example.pumppatrol.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.pumppatrol.R
import com.example.pumppatrol.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        val switchTheme = binding.switchTheme

        // Load saved theme preference
        val isDarkMode = sharedPreferences.getBoolean("DarkMode", false)
        switchTheme.isChecked = isDarkMode

        // Apply theme
        applyTheme(isDarkMode)

        // Prevent listener from triggering on initialization
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            saveThemePreference(isChecked)
            applyTheme(isChecked)
        }
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("DarkMode", isDarkMode)
            apply()
        }
    }

    private fun applyTheme(isDarkMode: Boolean) {
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // Change background dynamically
        val background = if (isDarkMode) R.drawable.background_dark else R.drawable.background_light
        requireActivity().window.decorView.setBackgroundResource(background)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
