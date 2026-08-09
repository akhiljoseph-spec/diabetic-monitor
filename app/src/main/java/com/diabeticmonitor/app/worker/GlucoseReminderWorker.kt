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
class GlucoseReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val sessionName = inputData.getString(KEY_SESSION_NAME) ?: "Glucose"
        showNotification(sessionName)
        return Result.success()
    }

    private fun showNotification(sessionName: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, DiabeticMonitorApp.CHANNEL_GLUCOSE_REMINDER)
            .setSmallIcon(R.drawable.ic_glucose)
            .setContentTitle("Time to log your glucose!")
            .setContentText("$sessionName reading is due. Tap to log now.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(sessionName.hashCode(), notification)
    }

    companion object {
        const val KEY_SESSION_NAME = "session_name"

        fun scheduleDaily(context: Context, sessionName: String, hourOfDay: Int, minute: Int): String {
            val tag = "glucose_reminder_$sessionName"
            val data = workDataOf(KEY_SESSION_NAME to sessionName)

            // Calculate initial delay
            val now = System.currentTimeMillis()
            val cal = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, hourOfDay)
                set(java.util.Calendar.MINUTE, minute)
                set(java.util.Calendar.SECOND, 0)
            }
            if (cal.timeInMillis <= now) cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
            val delay = cal.timeInMillis - now

            val request = PeriodicWorkRequestBuilder<GlucoseReminderWorker>(1, TimeUnit.DAYS)
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                tag, ExistingPeriodicWorkPolicy.UPDATE, request
            )
            return tag
        }

        fun cancelAll(context: Context, sessionName: String) {
            WorkManager.getInstance(context).cancelAllWorkByTag("glucose_reminder_$sessionName")
        }
    }
}
