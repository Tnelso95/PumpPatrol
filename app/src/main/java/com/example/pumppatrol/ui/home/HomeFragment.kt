package com.example.pumppatrol.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.pumppatrol.R
import com.example.pumppatrol.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val allMissions = mutableListOf(
        "Complete 5 Push-ups", "Run 1 Mile", "Do 20 Squats", "Hold a 30s Plank",
        "Do 10 Burpees", "Jump Rope for 2 Minutes", "Bike for 3 Miles", "Walk 5,000 Steps",
        "Do 15 Sit-ups", "Stretch for 5 Minutes", "Try a New Yoga Pose", "Sprint for 30 Seconds",
        "Drink 2 Extra Glasses of Water", "Do 3 Sets of 10 Lunges", "Do 10 Jump Squats",
        "Hold a 1-minute Wall Sit", "Perform 15 Tricep Dips", "Do 3x20 Bicycle Crunches",
        "Run Up and Down Stairs for 2 Minutes", "Do 25 Calf Raises"
    )

    private var availableMissions = allMissions.map { it to false }.toMutableList()
    private var currentMission: String? = null
    private var completedMissionsCount = 0

    private val achievementsList = mutableListOf<String>()
    private val achievementsThresholds = listOf(
        1 to "First Mission Completed",
        10 to "10 Missions Completed",
        50 to "50 Missions Completed",
        10 to "Ran 10 Miles Total",
        100 to "Performed 100 Push-ups",
        20 to "Completed All Missions"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // ✅ Show streak popup only once per day
        if (shouldShowStreakPopup()) {
            showStreakPopupIfNeeded()
        }

        val sharedPrefs = requireActivity().getSharedPreferences("calorie_prefs", Context.MODE_PRIVATE)

        // Reset calories and goal every launch
        sharedPrefs.edit()
            .putInt("caloriesConsumed", 0)
            .putInt("calorieGoal", 0)
            .apply()

        var consumed = 0
        var goal = 0

        val trackerButton = binding.btnOpenCalorieTracker
        val calorieInput = binding.etCalorieInput
        val addButton = binding.btnAddCalories
        val inputContainer = binding.calorieInputContainer
        val calorieProgressBar = binding.calorieProgressBar
        val calorieProgressText = binding.calorieProgressText

        // Init
        calorieProgressBar.progress = 0
        calorieProgressText.text = "$consumed / $goal calories"
        inputContainer.visibility = View.GONE

        trackerButton.setOnClickListener {
            inputContainer.visibility = View.VISIBLE
            calorieInput.hint = if (goal == 0) "Set Calorie Goal" else "Add Calories"
            calorieInput.requestFocus()
        }

        addButton.setOnClickListener {
            val input = calorieInput.text.toString()
            val number = input.toIntOrNull()

            if (goal == 0) {
                // Set goal
                if (number != null && number > 0) {
                    goal = number
                    consumed = 0
                    calorieProgressBar.progress = 0
                    calorieProgressText.text = "$consumed / $goal calories"
                    calorieInput.hint = "Add Calories"
                    calorieInput.text.clear()

                    sharedPrefs.edit()
                        .putInt("calorieGoal", goal)
                        .putInt("caloriesConsumed", consumed)
                        .apply()

                    Toast.makeText(requireContext(), "Goal set to $goal calories", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Enter a valid calorie goal", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Add calories
                if (number != null && number > 0) {
                    consumed += number
                    val progress = (consumed * 100 / goal).coerceAtMost(100)
                    calorieProgressBar.progress = progress
                    calorieProgressText.text = "$consumed / $goal calories"
                    calorieInput.text.clear()

                    sharedPrefs.edit()
                        .putInt("caloriesConsumed", consumed)
                        .apply()

                    Toast.makeText(requireContext(), "+$number calories added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Enter a valid number of calories", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.showMissionsButton.setOnClickListener {
            showMissionsBottomSheet(binding.selectedMissionText, binding.completeMissionButton)
        }

        binding.completeMissionButton.setOnClickListener {
            completeMission(binding.selectedMissionText, binding.completeMissionButton)
        }

        binding.achievementsButton.setOnClickListener {
            showAchievementsBottomSheet()
        }

        return root
    }

    private fun shouldShowStreakPopup(): Boolean {
        val prefs = requireActivity().getSharedPreferences("streak_prefs", Context.MODE_PRIVATE)
        val lastShownDate = prefs.getString("lastShownDate", null)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return if (lastShownDate != today) {
            prefs.edit().putString("lastShownDate", today).apply()
            true
        } else {
            false
        }
    }

    private fun showStreakPopupIfNeeded() {
        Toast.makeText(requireContext(), "🔥 Daily Streak: You're on a roll!", Toast.LENGTH_LONG).show()
    }

    private fun showMissionsBottomSheet(missionTextView: TextView, completeButton: Button) {
        if (availableMissions.isEmpty()) {
            availableMissions = allMissions.map { it to false }.toMutableList()
            Toast.makeText(requireContext(), "All missions completed! Resetting list.", Toast.LENGTH_SHORT).show()
        }

        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_missions, null)
        val listView: ListView = view.findViewById(R.id.missionsListView)

        val adapter = object : ArrayAdapter<Pair<String, Boolean>>(requireContext(), android.R.layout.simple_list_item_1, availableMissions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                val (mission, completed) = availableMissions[position]
                view.text = if (completed) "✅ $mission" else mission
                return view
            }
        }

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedMission = availableMissions[position].first
            currentMission = selectedMission
            missionTextView.text = selectedMission
            missionTextView.visibility = View.VISIBLE
            completeButton.visibility = View.VISIBLE
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun showAchievementsBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_achievements, null)
        val listView: ListView = view.findViewById(R.id.achievementsListView)

        achievementsList.clear()
        checkAchievements()

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, achievementsList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedAchievement = achievementsList[position]
            Toast.makeText(requireContext(), "Achievement: $selectedAchievement", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun checkAchievements() {
        for ((threshold, achievement) in achievementsThresholds) {
            if (completedMissionsCount >= threshold && !achievementsList.contains(achievement)) {
                achievementsList.add(achievement)
            }
        }
    }

    private fun completeMission(missionTextView: TextView, completeButton: Button) {
        if (currentMission != null) {
            availableMissions = availableMissions.map {
                if (it.first == currentMission) it.copy(second = true) else it
            }.toMutableList()

            completedMissionsCount++
            Toast.makeText(requireContext(), "Mission Completed! ✅", Toast.LENGTH_SHORT).show()

            missionTextView.text = "No mission selected"
            completeButton.visibility = View.GONE

            showMissionsBottomSheet(missionTextView, completeButton)
            showAchievementsBottomSheet()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
