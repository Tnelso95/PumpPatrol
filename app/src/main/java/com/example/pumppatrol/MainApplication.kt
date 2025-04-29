package com.example.pumppatrol

import android.app.Application

class MainApplication : Application() {
    var hasShownStreakPopup = false

    override fun onCreate() {
        super.onCreate()
        // Initialize anything app-wide here if needed later
    }
}
