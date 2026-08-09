package com.diabeticmonitor.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diabeticmonitor.app.data.db.entity.UserProfile
import com.diabeticmonitor.app.data.repository.GlucoseRepository
import com.diabeticmonitor.app.data.repository.MedicationRepository
import com.diabeticmonitor.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val profileRepo: UserProfileRepository,
    private val glucoseRepo: GlucoseRepository,
    private val medRepo: MedicationRepository
) : ViewModel() {

    val profile = profileRepo.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch { profileRepo.saveProfile(profile) }
    }

    fun exportPdf(
        context: android.content.Context,
        onSuccess: (android.content.Intent) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val readings = mutableListOf<com.diabeticmonitor.app.data.db.entity.GlucoseReading>()
            val meds = mutableListOf<com.diabeticmonitor.app.data.db.entity.MedicationEntry>()
            glucoseRepo.getAllReadings().collect { readings.addAll(it) }
            // Use a snapshot
            com.diabeticmonitor.app.util.PdfExporter.generateAndShare(
                context, readings, meds, profile.value, onSuccess, onError
            )
        }
    }
}
