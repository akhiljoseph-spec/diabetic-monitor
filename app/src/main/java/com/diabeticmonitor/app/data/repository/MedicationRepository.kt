package com.diabeticmonitor.app.data.repository

import com.diabeticmonitor.app.data.db.dao.MedicationEntryDao
import com.diabeticmonitor.app.data.db.entity.MedicationEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationRepository @Inject constructor(
    private val dao: MedicationEntryDao
) {
    fun getAllEntries(): Flow<List<MedicationEntry>> = dao.getAllEntries()

    fun getEntriesForRange(start: Long, end: Long): Flow<List<MedicationEntry>> =
        dao.getEntriesForRange(start, end)

    fun getPendingEntries(): Flow<List<MedicationEntry>> = dao.getPendingEntries()

    fun getRecentEntries(limit: Int = 30): Flow<List<MedicationEntry>> =
        dao.getRecentEntries(limit)

    suspend fun insert(entry: MedicationEntry): Long = dao.insert(entry)

    suspend fun update(entry: MedicationEntry) = dao.update(entry)

    suspend fun delete(entry: MedicationEntry) = dao.delete(entry)

    suspend fun markAsTaken(id: Long) = dao.markAsTaken(id)
}
