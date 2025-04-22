package com.example.pumppatrol.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pumppatrol.AvatarView
import com.example.pumppatrol.R
import com.example.pumppatrol.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Full list of missions, now paired with a boolean to track completion
    private val allMissions = mutableListOf(
        "Complete 5 Push-ups", "Run 1 Mile", "Do 20 Squats", "Hold a 30s Plank",
        "Do 10 Burpees", "Jump Rope for 2 Minutes", "Bike for 3 Miles", "Walk 5,000 Steps",
        "Do 15 Sit-ups", "Stretch for 5 Minutes", "Try a New Yoga Pose", "Sprint for 30 Seconds",
        "Drink 2 Extra Glasses of Water", "Do 3 Sets of 10 Lunges", "Do 10 Jump Squats",
        "Hold a 1-minute Wall Sit", "Perform 15 Tricep Dips", "Do 3x20 Bicycle Crunches",
        "Run Up and Down Stairs for 2 Minutes", "Do 25 Calf Raises"
    )

    private var availableMissions = allMissions.map { it to false }.toMutableList() // Pair<String, Boolean>
    private var currentMission: String? = null
    private var completedMissionsCount = 0 // Track the number of completed missions

    // Achievements List
    private val achievementsList = mutableListOf<String>()

    private val achievementsThresholds = listOf(
        1 to "First Mission Completed",
        10 to "10 Missions Completed",
        50 to "50 Missions Completed",
        10 to "Ran 10 Miles Total", // You can track specific missions for distance, e.g. running 1 mile
        100 to "Performed 100 Push-ups", // Track push-up related missions
        20 to "Completed All Missions" // Track total completed missions
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val showMissionsButton: Button = binding.showMissionsButton
        val selectedMissionText: TextView = binding.selectedMissionText
        val completeMissionButton: Button = binding.completeMissionButton
        val achievementsButton: Button = binding.achievementsButton

        // Load and apply saved avatar
        val sharedPref = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val savedBodyResId = sharedPref.getInt("avatar_body", R.drawable.body2)
        val avatarView = binding.homeAvatar // Assuming the view ID is `homeAvatar` in XML
        avatarView.updateAvatar(savedBodyResId)



        showMissionsButton.setOnClickListener {
            showMissionsBottomSheet(selectedMissionText, completeMissionButton)
        }

        completeMissionButton.setOnClickListener {
            completeMission(selectedMissionText, completeMissionButton)
        }

        achievementsButton.setOnClickListener {
            showAchievementsBottomSheet()
        }

        return root
    }

    private fun showMissionsBottomSheet(missionTextView: TextView, completeButton: Button) {
        if (availableMissions.isEmpty()) {
            availableMissions = allMissions.map { it to false }.toMutableList() // Reset missions
            Toast.makeText(requireContext(), "All missions completed! Resetting list.", Toast.LENGTH_SHORT).show()
        }

        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_missions, null)
        val listView: ListView = view.findViewById(R.id.missionsListView)

        // Custom adapter to display missions with checkmarks for completed ones
        val adapter = object : ArrayAdapter<Pair<String, Boolean>>(requireContext(), android.R.layout.simple_list_item_1, availableMissions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                val (mission, completed) = availableMissions[position]
                view.text = if (completed) "✅ $mission" else mission // Add checkmark if completed
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

        // Refresh achievements list
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
        // Check if new achievements should be unlocked based on completed missions
        for ((threshold, achievement) in achievementsThresholds) {
            if (completedMissionsCount >= threshold && !achievementsList.contains(achievement)) {
                achievementsList.add(achievement)
            }
        }
    }

    private fun completeMission(missionTextView: TextView, completeButton: Button) {
        if (currentMission != null) {
            // Find the mission in the list and mark it as completed
            availableMissions = availableMissions.map {
                if (it.first == currentMission) it.copy(second = true) else it
            }.toMutableList()

            // Increment completed mission count
            completedMissionsCount++

            Toast.makeText(requireContext(), "Mission Completed! ✅", Toast.LENGTH_SHORT).show()

            missionTextView.text = "No mission selected"
            completeButton.visibility = View.GONE

            // Refresh the missions list to show checkmarks
            showMissionsBottomSheet(missionTextView, completeButton)

            // Update achievements
            showAchievementsBottomSheet()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
