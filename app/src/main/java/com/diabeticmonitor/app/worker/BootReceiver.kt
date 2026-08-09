package com.diabeticmonitor.app.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.diabeticmonitor.app.data.db.entity.SessionType
import com.diabeticmonitor.app.worker.GlucoseReminderWorker

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule all glucose reminders
            SessionType.values().forEach { session ->
                GlucoseReminderWorker.scheduleDaily(
                    context,
                    session.displayName,
                    session.reminderHour,
                    session.reminderMinute
                )
            }
        }
    }
}
