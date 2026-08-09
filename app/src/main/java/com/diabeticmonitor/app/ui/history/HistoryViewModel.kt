package com.diabeticmonitor.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diabeticmonitor.app.data.db.entity.GlucoseReading
import com.diabeticmonitor.app.data.repository.GlucoseRepository
import com.diabeticmonitor.app.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HistoryFilter { ALL, TODAY, WEEK, MONTH }

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: GlucoseRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(HistoryFilter.ALL)

    val readings: StateFlow<List<GlucoseReading>> = _filter.flatMapLatest { filter ->
        val now = System.currentTimeMillis()
        when (filter) {
            HistoryFilter.TODAY -> repo.getReadingsForDay(DateTimeUtils.getStartOfDay(), DateTimeUtils.getEndOfDay())
            HistoryFilter.WEEK  -> repo.getReadingsForRange(now - 7 * 24 * 3600 * 1000L, now)
            HistoryFilter.MONTH -> repo.getReadingsForRange(DateTimeUtils.getStartOfMonth(), now)
            HistoryFilter.ALL   -> repo.getAllReadings()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(f: HistoryFilter) { _filter.value = f }

    fun delete(reading: GlucoseReading) {
        viewModelScope.launch { repo.delete(reading) }
    }
}
