package com.diabeticmonitor.app.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diabeticmonitor.app.data.db.entity.GlucoseReading
import com.diabeticmonitor.app.data.repository.GlucoseRepository
import com.diabeticmonitor.app.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

enum class ChartPeriod { DAY, MONTH, YEAR }

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val repo: GlucoseRepository
) : ViewModel() {

    private val _period = MutableStateFlow(ChartPeriod.DAY)

    val readings: StateFlow<List<GlucoseReading>> = _period.flatMapLatest { period ->
        val now = System.currentTimeMillis()
        when (period) {
            ChartPeriod.DAY   -> repo.getReadingsForDay(DateTimeUtils.getStartOfDay(), DateTimeUtils.getEndOfDay())
            ChartPeriod.MONTH -> repo.getReadingsForRange(DateTimeUtils.getStartOfMonth(), now)
            ChartPeriod.YEAR  -> repo.getReadingsForRange(DateTimeUtils.getStartOfYear(), now)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setPeriod(p: ChartPeriod) { _period.value = p }
    fun getCurrentPeriod() = _period.value
}
