package com.example.pumppatrol

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pumppatrol.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)

        // Apply saved theme preference
        val isDarkMode = sharedPreferences.getBoolean("DarkMode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        // Inflate and set the view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply the correct background
        applyBackground(isDarkMode)

        // Initialize UI components
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)

        val loginLayout = findViewById<View>(R.id.login_layout)
        val mainContent = findViewById<View>(R.id.main_content)

        // Check login state before setting visibility
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            loginLayout.visibility = View.GONE
            mainContent.visibility = View.VISIBLE
        } else {
            loginLayout.visibility = View.VISIBLE
            mainContent.visibility = View.GONE
        }

        // Handle login button click
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Save login state in SharedPreferences
                with(sharedPreferences.edit()) {
                    putBoolean("isLoggedIn", true)
                    apply()
                }

                // Show main content and hide login page
                loginLayout.visibility = View.GONE
                mainContent.visibility = View.VISIBLE
            }
        }

        // Navigation setup
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_workout, R.id.navigation_history, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    override fun onResume() {
        super.onResume()
        val isDarkMode = sharedPreferences.getBoolean("DarkMode", false)
        applyBackground(isDarkMode)
    }

    private fun applyBackground(isDarkMode: Boolean) {
        val backgroundRes = if (isDarkMode) R.drawable.background_dark else R.drawable.background_light
        binding.root.setBackgroundResource(backgroundRes)
    }
}
