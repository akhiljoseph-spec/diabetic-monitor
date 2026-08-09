package com.diabeticmonitor.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val age: Int = 0,
    val diabetesType: String = "Type 2",
    val doctorName: String = "",
    val doctorNotes: String = "",
    val targetMinGlucose: Float = 70f,
    val targetMaxGlucose: Float = 140f,
    val enableNotifications: Boolean = true,
    val useDarkTheme: Boolean = false
)
