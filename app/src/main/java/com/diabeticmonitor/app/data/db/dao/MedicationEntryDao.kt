package com.diabeticmonitor.app.data.db.dao

import androidx.room.*
import com.diabeticmonitor.app.data.db.entity.MedicationEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MedicationEntry): Long

    @Update
    suspend fun update(entry: MedicationEntry)

    @Delete
    suspend fun delete(entry: MedicationEntry)

    @Query("SELECT * FROM medication_entries ORDER BY scheduledTime DESC")
    fun getAllEntries(): Flow<List<MedicationEntry>>

    @Query("SELECT * FROM medication_entries WHERE timestamp(scheduledTime) = timestamp(:date)")
    fun getEntriesForDate(date: Long): Flow<List<MedicationEntry>>

    @Query("SELECT * FROM medication_entries WHERE scheduledTime BETWEEN :start AND :end ORDER BY scheduledTime ASC")
    fun getEntriesForRange(start: Long, end: Long): Flow<List<MedicationEntry>>

    @Query("SELECT * FROM medication_entries WHERE isTaken = 0 ORDER BY scheduledTime ASC")
    fun getPendingEntries(): Flow<List<MedicationEntry>>

    @Query("UPDATE medication_entries SET isTaken = 1, takenTime = :takenTime WHERE id = :id")
    suspend fun markAsTaken(id: Long, takenTime: Long = System.currentTimeMillis())

    @Query("SELECT * FROM medication_entries ORDER BY scheduledTime DESC LIMIT :limit")
    fun getRecentEntries(limit: Int = 30): Flow<List<MedicationEntry>>
}
