package com.example.pumppatrol

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pumppatrol.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var streakTextView: TextView

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

        streakTextView = findViewById(R.id.streak_text)
        loginStreak()

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

    override fun onResume() {
        super.onResume()
        val isDarkMode = sharedPreferences.getBoolean("DarkMode", false)
        applyBackground(isDarkMode)
    }

    private fun applyBackground(isDarkMode: Boolean) {
        val backgroundRes = if (isDarkMode) R.drawable.background_dark else R.drawable.background_light
        binding.root.setBackgroundResource(backgroundRes)
    }



    private fun showStreakPopup(streakCount: Int, badgeMessage: String?) {
        val alertDialog = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Daily Login Streak")

        var message = "Welcome back! Your streak is now $streakCount days. Keep going! 🔥"
        if (badgeMessage != null) {
            message += "\n\n🏆 You've earned a badge!\n$badgeMessage"
        }

        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }




    private fun loginStreak() {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastLoginDate = sharedPreferences.getString("lastLoginDate", null)
        var streakCount = sharedPreferences.getInt("streakCount", 0)

        if (lastLoginDate == null) {
            streakCount = 1  // first login
        } else {
            val lastDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(lastLoginDate)
            val calendar = Calendar.getInstance()
            calendar.time = lastDate!!

            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val expectedNextLogin = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            if (todayDate == expectedNextLogin) {
                streakCount++  // continue streak
            } else if (todayDate != lastLoginDate) {
                streakCount = 1  // reset streak
            }
        }

        // save streak
        sharedPreferences.edit()
            .putString("lastLoginDate", todayDate)
            .putInt("streakCount", streakCount)
            .apply()

        // Show pop-up after login
        // Check for badge
        val badgeMessage = getBadgeForStreak(streakCount)

        // Show pop-up with or without a badge
        showStreakPopup(streakCount, badgeMessage)
    }

    private fun getBadgeForStreak(streakCount: Int): String? {
        return when (streakCount) {
            1 -> "🎉 Welcome to Pump Patrol! Today is only day one, so let's get to work!"
            10 -> "🔥 10-Day Streak! You're on fire!"
            25 -> "🏅 25-Day Streak! Nothing can stop you now!"
            50 -> "💎 50 Days In a Row! You're unstoppable!"
            100 -> "🌟 100 Days! You are an elite exerciser!"
            365 -> "💪 One whole year! No days off, you are officially in G.O.A.T. status!"
            else -> null
        }
    }



}
