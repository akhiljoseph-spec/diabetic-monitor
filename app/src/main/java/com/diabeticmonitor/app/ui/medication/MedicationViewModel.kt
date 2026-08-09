package com.diabeticmonitor.app.ui.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diabeticmonitor.app.data.db.entity.MedicationEntry
import com.diabeticmonitor.app.data.repository.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicationViewModel @Inject constructor(
    private val repo: MedicationRepository
) : ViewModel() {

    val allMedications = repo.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingMedications = repo.getPendingEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMedication(entry: MedicationEntry) {
        viewModelScope.launch { repo.insert(entry) }
    }

    fun markAsTaken(id: Long) {
        viewModelScope.launch { repo.markAsTaken(id) }
    }

    fun delete(entry: MedicationEntry) {
        viewModelScope.launch { repo.delete(entry) }
    }
}
