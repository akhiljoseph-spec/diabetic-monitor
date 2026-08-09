package com.diabeticmonitor.app.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.diabeticmonitor.app.data.db.dao.GlucoseReadingDao
import com.diabeticmonitor.app.data.db.dao.MedicationEntryDao
import com.diabeticmonitor.app.data.db.dao.UserProfileDao
import com.diabeticmonitor.app.data.db.entity.GlucoseReading
import com.diabeticmonitor.app.data.db.entity.MedicationEntry
import com.diabeticmonitor.app.data.db.entity.UserProfile

@Database(
    entities = [GlucoseReading::class, MedicationEntry::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun glucoseReadingDao(): GlucoseReadingDao
    abstract fun medicationEntryDao(): MedicationEntryDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        const val DATABASE_NAME = "diabetic_monitor_db"
    }
}
