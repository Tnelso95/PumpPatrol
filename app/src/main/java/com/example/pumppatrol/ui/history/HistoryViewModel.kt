
package com.example.pumppatrol.ui.history

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import java.util.Locale

class HistoryViewModel : ViewModel() {

    // Now holds SpannableStringBuilder so we can bold parts of it
    private val _workoutHistory = MutableLiveData<SpannableStringBuilder>()
    val workoutHistory: LiveData<SpannableStringBuilder> = _workoutHistory

    init {
        fetchWorkoutHistory()
    }

    private fun fetchWorkoutHistory() {
        val myRef = FirebaseDatabase.getInstance().getReference("WorkoutHistory")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ssb = SpannableStringBuilder()
                val workouts = snapshot.children.toList().asReversed()

                // month names for prettier dates
                val months = listOf(
                    "Jan","Feb","Mar","Apr","May","Jun",
                    "Jul","Aug","Sep","Oct","Nov","Dec"
                )

                for (workoutSnapshot in workouts) {
                    // rawTitle looks like "Workout_2025-04-21_16-22"
                    val rawTitle = workoutSnapshot
                        .child("title")
                        .getValue(String::class.java)
                        ?: continue
                    val totalTime = workoutSnapshot
                        .child("totalTime")
                        .getValue(Long::class.java)
                        ?: continue

                    // strip prefix & split into date + time
                    val parts = rawTitle
                        .removePrefix("Workout_")
                        .split("_", limit = 2)

                    val prettyDateTime = if (parts.size == 2) {
                        val (datePart, timePart) = parts

                        // format "2025-04-21" → "Apr 21, 2025"
                        val dp = datePart.split("-")
                        val prettyDate = if (dp.size == 3) {
                            val y = dp[0].toIntOrNull()
                            val m = dp[1].toIntOrNull()
                            val d = dp[2].toIntOrNull()
                            if (y != null && m != null && m in 1..12 && d != null) {
                                // now m is smart‐cast to non‑null Int
                                "${months[m - 1]} $d, $y"
                            } else datePart
                        } else datePart

                        // format "16-22" → "4:22 PM"
                        val tp = timePart.split("-", limit = 2)
                        val prettyTime = if (tp.size == 2) {
                            val hour = tp[0].toIntOrNull() ?: 0
                            val minute = tp[1].padStart(2, '0')
                            val ampm = if (hour >= 12) "PM" else "AM"
                            val h12 = if (hour % 12 == 0) 12 else hour % 12
                            "$h12:$minute $ampm"
                        } else timePart

                        "$prettyDate  $prettyTime"
                    } else rawTitle

                    // BOLD the title line
                    val start = ssb.length
                    ssb.append("• $prettyDateTime\n")
                    ssb.setSpan(
                        StyleSpan(Typeface.BOLD),
                        start,
                        ssb.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    // Duration
                    ssb.append("    Duration: ${formatTime(totalTime)}\n")

                    // Each exercise + sets
                    val exercisesNode = workoutSnapshot.child("exercises")
                    for (exerciseChild in exercisesNode.children) {
                        val name = exerciseChild
                            .child("name")
                            .getValue(String::class.java)
                            ?: "Exercise"
                        ssb.append("      - $name\n")
                        for (setChild in exerciseChild.child("sets").children) {
                            val setNum = setChild
                                .child("setNumber")
                                .getValue(Int::class.java)
                                ?: continue
                            val weight = setChild
                                .child("weight")
                                .getValue(Float::class.java)
                                ?: continue
                            val reps = setChild
                                .child("reps")
                                .getValue(Int::class.java)
                                ?: continue
                            ssb.append(
                                "          Set $setNum: $weight lb x $reps reps\n"
                            )
                        }
                    }

                    // extra spacing between workouts
                    ssb.append("\n")
                }

                _workoutHistory.value = ssb
            }

            override fun onCancelled(error: DatabaseError) {
                _workoutHistory.value =
                    SpannableStringBuilder("Error loading history: ${error.message}")
            }
        })
    }

    private fun formatTime(milliseconds: Long): String {
        val minutes = (milliseconds / 60000).toInt()
        val seconds = ((milliseconds % 60000) / 1000).toInt()
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}
