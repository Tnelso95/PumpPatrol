package com.example.pumppatrol

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.pumppatrol.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var streakTextView: TextView
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate and set the view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)




        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)

        // Apply saved theme preference
        applyTheme()

        // Set up navigation
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_workout, R.id.navigation_history)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)

        // Initialize UI components
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)

        val loginLayout = findViewById<View>(R.id.login_layout)
        val mainContent = findViewById<View>(R.id.main_content)

        // Check login state
        checkLoginState(loginLayout, mainContent)

        // Handle login button click
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                with(sharedPreferences.edit()) {
                    putBoolean("isLoggedIn", true)
                    apply()
                }
                loginLayout.visibility = View.GONE
                mainContent.visibility = View.VISIBLE
                loginStreak() // Update login streak after successful login
            }
        }
    }

    private fun applyTheme() {
        val isDarkMode = sharedPreferences.getBoolean("DarkMode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        applyBackground(isDarkMode)
    }

    private fun checkLoginState(loginLayout: View, mainContent: View) {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            loginLayout.visibility = View.GONE
            mainContent.visibility = View.VISIBLE
            loginStreak() // Update streak on resume if logged in
        } else {
            loginLayout.visibility = View.VISIBLE
            mainContent.visibility = View.GONE
        }
    }

    private fun applyBackground(isDarkMode: Boolean) {
        val backgroundRes = if (isDarkMode) R.drawable.background_dark else R.drawable.background_light
        binding.root.setBackgroundResource(backgroundRes)
    }

    private fun showStreakPopup(streakCount: Int, badgeMessage: String?) {
        val alertDialog = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Daily Login Streak")

        var message = "Welcome back! Your streak is now $streakCount days. Keep going! \uD83D\uDD25"
        if (badgeMessage != null) {
            message += "\n\n\uD83C\uDFC6 You've earned a badge!\n$badgeMessage"
        }

        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun loginStreak() {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastLoginDate = sharedPreferences.getString("lastLoginDate", null)
        var streakCount = sharedPreferences.getInt("streakCount", 0)

        if (lastLoginDate == null) {
            streakCount = 1
        } else {
            val lastDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(lastLoginDate)
            val calendar = Calendar.getInstance()
            calendar.time = lastDate!!
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val expectedNextLogin = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            if (todayDate == expectedNextLogin) {
                streakCount++
            } else if (todayDate != lastLoginDate) {
                streakCount = 1
            }
        }

        sharedPreferences.edit()
            .putString("lastLoginDate", todayDate)
            .putInt("streakCount", streakCount)
            .apply()

        val badgeMessage = getBadgeForStreak(streakCount)
        showStreakPopup(streakCount, badgeMessage)
    }

    private fun getBadgeForStreak(streakCount: Int): String? {
        return when (streakCount) {
            1 -> "\uD83C\uDF89 Welcome to Pump Patrol! Today is only day one, so let's get to work!"
            10 -> "\uD83D\uDD25 10-Day Streak! You're on fire!"
            25 -> "\uD83C\uDFC5 25-Day Streak! Nothing can stop you now!"
            50 -> "\uD83D\uDC8E 50 Days In a Row! You're unstoppable!"
            100 -> "\uD83C\uDF1F 100 Days! You are an elite exerciser!"
            365 -> "\uD83D\uDCAA One whole year! No days off, you are officially in G.O.A.T. status!"
            else -> null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.navigation_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        // Apply theme on resume
        applyTheme()

        // Re-check login state and update UI accordingly
        val loginLayout = findViewById<View>(R.id.login_layout)
        val mainContent = findViewById<View>(R.id.main_content)
        checkLoginState(loginLayout, mainContent)
    }
}
