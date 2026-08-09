package com.diabeticmonitor.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DiabeticMonitorApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val glucoseChannel = NotificationChannel(
                CHANNEL_GLUCOSE_REMINDER,
                "Glucose Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to log your glucose readings"
            }

            val medicationChannel = NotificationChannel(
                CHANNEL_MEDICATION_REMINDER,
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to take your medications"
            }

            manager.createNotificationChannels(listOf(glucoseChannel, medicationChannel))
        }
    }

    companion object {
        const val CHANNEL_GLUCOSE_REMINDER = "glucose_reminder"
        const val CHANNEL_MEDICATION_REMINDER = "medication_reminder"
    }
}
