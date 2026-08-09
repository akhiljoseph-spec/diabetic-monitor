package com.diabeticmonitor.app.data.repository

import com.diabeticmonitor.app.data.db.dao.GlucoseReadingDao
import com.diabeticmonitor.app.data.db.entity.GlucoseReading
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlucoseRepository @Inject constructor(
    private val dao: GlucoseReadingDao
) {
    fun getAllReadings(): Flow<List<GlucoseReading>> = dao.getAllReadings()

    fun getReadingsForDay(startOfDay: Long, endOfDay: Long): Flow<List<GlucoseReading>> =
        dao.getReadingsForDay(startOfDay, endOfDay)

    fun getReadingsForRange(start: Long, end: Long): Flow<List<GlucoseReading>> =
        dao.getReadingsForRange(start, end)

    fun getRecentReadings(limit: Int = 50): Flow<List<GlucoseReading>> =
        dao.getRecentReadings(limit)

    suspend fun insert(reading: GlucoseReading): Long = dao.insert(reading)

    suspend fun update(reading: GlucoseReading) = dao.update(reading)

    suspend fun delete(reading: GlucoseReading) = dao.delete(reading)

    suspend fun getLatestForSession(sessionType: String): GlucoseReading? =
        dao.getLatestForSession(sessionType)

    suspend fun getAverageForRange(start: Long, end: Long): Float? =
        dao.getAverageForRange(start, end)

    suspend fun getMinForRange(start: Long, end: Long): Float? =
        dao.getMinForRange(start, end)

    suspend fun getMaxForRange(start: Long, end: Long): Float? =
        dao.getMaxForRange(start, end)
}
