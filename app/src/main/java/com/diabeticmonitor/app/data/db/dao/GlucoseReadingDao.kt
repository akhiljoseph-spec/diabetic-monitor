package com.diabeticmonitor.app.data.db.dao

import androidx.room.*
import com.diabeticmonitor.app.data.db.entity.GlucoseReading
import kotlinx.coroutines.flow.Flow

@Dao
interface GlucoseReadingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reading: GlucoseReading): Long

    @Update
    suspend fun update(reading: GlucoseReading)

    @Delete
    suspend fun delete(reading: GlucoseReading)

    @Query("SELECT * FROM glucose_readings ORDER BY timestamp DESC")
    fun getAllReadings(): Flow<List<GlucoseReading>>

    @Query("SELECT * FROM glucose_readings WHERE timestamp BETWEEN :startOfDay AND :endOfDay ORDER BY timestamp ASC")
    fun getReadingsForDay(startOfDay: Long, endOfDay: Long): Flow<List<GlucoseReading>>

    @Query("SELECT * FROM glucose_readings WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    fun getReadingsForRange(start: Long, end: Long): Flow<List<GlucoseReading>>

    @Query("SELECT * FROM glucose_readings WHERE sessionType = :sessionType ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestForSession(sessionType: String): GlucoseReading?

    @Query("SELECT AVG(glucoseLevel) FROM glucose_readings WHERE timestamp BETWEEN :start AND :end")
    suspend fun getAverageForRange(start: Long, end: Long): Float?

    @Query("SELECT MIN(glucoseLevel) FROM glucose_readings WHERE timestamp BETWEEN :start AND :end")
    suspend fun getMinForRange(start: Long, end: Long): Float?

    @Query("SELECT MAX(glucoseLevel) FROM glucose_readings WHERE timestamp BETWEEN :start AND :end")
    suspend fun getMaxForRange(start: Long, end: Long): Float?

    @Query("SELECT * FROM glucose_readings ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentReadings(limit: Int = 50): Flow<List<GlucoseReading>>
}
