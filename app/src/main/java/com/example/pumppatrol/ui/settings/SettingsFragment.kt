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

        val avatarView = binding.avatarView

        // Check if the avatarView is ready and attached
        if (avatarView.isAttachedToWindow) {
            avatarView.updateAvatar(
                R.drawable.base1
            )
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        val avatarView = binding.avatarView
        val switchTheme = binding.switchTheme

        // Load avatar body from SharedPreferences
        val savedAvatar = sharedPreferences.getInt("avatar_body", R.drawable.body2)
        avatarView.updateAvatar(savedAvatar)

        // Theme setup (same as before)
        val isDarkMode = sharedPreferences.getBoolean("DarkMode", false)
        switchTheme.isChecked = isDarkMode
        applyTheme(isDarkMode)

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            saveThemePreference(isChecked)
            applyTheme(isChecked)
        }

        // Avatar click listener
        avatarView.setOnClickListener {
            showAvatarSelectionDialog()
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

    private fun showAvatarSelectionDialog() {
        val avatarOptions = arrayOf("Body 1", "Body 2", "Body 3")
        val avatarDrawables = arrayOf(
            R.drawable.body1,
            R.drawable.body2,
            R.drawable.body3
        )

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Choose Avatar Body")
            .setItems(avatarOptions) { _, which ->
                val selectedRes = avatarDrawables[which]
                binding.avatarView.updateAvatar(selectedRes)

                // Save the selected avatar
                with(sharedPreferences.edit()) {
                    putInt("avatar_body", selectedRes)
                    apply()
                }
            }
            .show()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
