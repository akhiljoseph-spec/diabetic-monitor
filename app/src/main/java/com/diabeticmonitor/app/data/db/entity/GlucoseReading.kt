package com.diabeticmonitor.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SessionType(val displayName: String, val reminderHour: Int, val reminderMinute: Int) {
    FASTING("Fasting", 7, 0),
    AFTER_BREAKFAST("After Breakfast (2hr)", 9, 30),
    AFTER_LUNCH("After Lunch (2hr)", 14, 0),
    BEFORE_DINNER("Before Dinner", 19, 0)
}

enum class GlucoseStatus { NORMAL, HIGH, LOW }

@Entity(tableName = "glucose_readings")
data class GlucoseReading(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionType: String,
    val glucoseLevel: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val unit: String = "mg/dL"
) {
    val status: GlucoseStatus
        get() = when {
            glucoseLevel < 70f -> GlucoseStatus.LOW
            glucoseLevel > 140f -> GlucoseStatus.HIGH
            else -> GlucoseStatus.NORMAL
        }
}
