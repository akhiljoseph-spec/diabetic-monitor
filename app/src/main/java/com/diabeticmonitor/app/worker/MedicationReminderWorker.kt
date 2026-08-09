package com.diabeticmonitor.app.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.diabeticmonitor.app.DiabeticMonitorApp
import com.diabeticmonitor.app.R
import com.diabeticmonitor.app.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class MedicationReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val medName = inputData.getString(KEY_MED_NAME) ?: "Medication"
        val dose = inputData.getString(KEY_DOSE) ?: ""
        showNotification(medName, dose)
        return Result.success()
    }

    private fun showNotification(medName: String, dose: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "medication")
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, DiabeticMonitorApp.CHANNEL_MEDICATION_REMINDER)
            .setSmallIcon(R.drawable.ic_medication)
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take $medName${if (dose.isNotEmpty()) " ($dose)" else ""}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(medName.hashCode(), notification)
    }

    companion object {
        const val KEY_MED_NAME = "med_name"
        const val KEY_DOSE = "dose"

        fun scheduleDaily(context: Context, medName: String, dose: String, hourOfDay: Int, minute: Int) {
            val tag = "med_reminder_${medName}_${hourOfDay}_$minute"
            val data = workDataOf(KEY_MED_NAME to medName, KEY_DOSE to dose)

            val now = System.currentTimeMillis()
            val cal = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, hourOfDay)
                set(java.util.Calendar.MINUTE, minute)
                set(java.util.Calendar.SECOND, 0)
            }
            if (cal.timeInMillis <= now) cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
            val delay = cal.timeInMillis - now

            val request = PeriodicWorkRequestBuilder<MedicationReminderWorker>(1, TimeUnit.DAYS)
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                tag, ExistingPeriodicWorkPolicy.UPDATE, request
            )
        }
    }
}
