package com.diabeticmonitor.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diabeticmonitor.app.data.db.entity.GlucoseReading
import com.diabeticmonitor.app.data.db.entity.SessionType
import com.diabeticmonitor.app.data.repository.GlucoseRepository
import com.diabeticmonitor.app.data.repository.UserProfileRepository
import com.diabeticmonitor.app.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val glucoseRepository: GlucoseRepository,
    private val profileRepository: UserProfileRepository
) : ViewModel() {

    val todayReadings = glucoseRepository.getReadingsForDay(
        DateTimeUtils.getStartOfDay(),
        DateTimeUtils.getEndOfDay()
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile = profileRepository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentReadings = glucoseRepository.getRecentReadings(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReading(reading: GlucoseReading) {
        viewModelScope.launch { glucoseRepository.insert(reading) }
    }

    fun deleteReading(reading: GlucoseReading) {
        viewModelScope.launch { glucoseRepository.delete(reading) }
    }

    fun getSessionReadings(sessionType: SessionType, readings: List<GlucoseReading>): GlucoseReading? =
        readings.filter { it.sessionType == sessionType.name }.maxByOrNull { it.timestamp }
}
