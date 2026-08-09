package com.diabeticmonitor.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MedicationType(val displayName: String) {
    INSULIN("Insulin"),
    TABLET("Tablet")
}

@Entity(tableName = "medication_entries")
data class MedicationEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicationType: String,
    val name: String,
    val dose: String,
    val unit: String = "",
    val scheduledTime: Long = System.currentTimeMillis(),
    val takenTime: Long? = null,
    val isTaken: Boolean = false,
    val notes: String = ""
)
