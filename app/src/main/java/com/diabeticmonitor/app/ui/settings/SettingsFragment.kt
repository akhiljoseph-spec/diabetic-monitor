package com.diabeticmonitor.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.diabeticmonitor.app.data.db.entity.SessionType
import com.diabeticmonitor.app.data.db.entity.UserProfile
import com.diabeticmonitor.app.databinding.FragmentSettingsBinding
import com.diabeticmonitor.app.worker.GlucoseReminderWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveProfile.setOnClickListener { saveProfile() }
        binding.btnExportPdf.setOnClickListener { exportPdf() }
        binding.switchNotifications.setOnCheckedChangeListener { _, checked ->
            if (checked) scheduleAllReminders() else cancelAllReminders()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profile.collect { profile ->
                    profile?.let { populateFields(it) }
                }
            }
        }
    }

    private fun populateFields(profile: UserProfile) {
        binding.etName.setText(profile.name)
        binding.etAge.setText(if (profile.age > 0) profile.age.toString() else "")
        binding.etDiabetesType.setText(profile.diabetesType)
        binding.etDoctorName.setText(profile.doctorName)
        binding.etDoctorNotes.setText(profile.doctorNotes)
        binding.switchNotifications.isChecked = profile.enableNotifications
    }

    private fun saveProfile() {
        val profile = UserProfile(
            name = binding.etName.text.toString().trim(),
            age = binding.etAge.text.toString().toIntOrNull() ?: 0,
            diabetesType = binding.etDiabetesType.text.toString().trim().ifEmpty { "Type 2" },
            doctorName = binding.etDoctorName.text.toString().trim(),
            doctorNotes = binding.etDoctorNotes.text.toString().trim(),
            enableNotifications = binding.switchNotifications.isChecked
        )
        viewModel.saveProfile(profile)
        Toast.makeText(requireContext(), "Profile saved!", Toast.LENGTH_SHORT).show()
    }

    private fun exportPdf() {
        viewModel.exportPdf(
            requireContext(),
            onSuccess = { intent -> startActivity(intent) },
            onError = { msg -> Toast.makeText(requireContext(), "Export failed: $msg", Toast.LENGTH_LONG).show() }
        )
    }

    private fun scheduleAllReminders() {
        SessionType.values().forEach { session ->
            GlucoseReminderWorker.scheduleDaily(
                requireContext(), session.displayName, session.reminderHour, session.reminderMinute
            )
        }
        Toast.makeText(requireContext(), "Reminders enabled", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAllReminders() {
        SessionType.values().forEach { session ->
            GlucoseReminderWorker.cancelAll(requireContext(), session.displayName)
        }
        Toast.makeText(requireContext(), "Reminders disabled", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
