package com.diabeticmonitor.app.ui.dialog

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.diabeticmonitor.app.data.db.entity.MedicationEntry
import com.diabeticmonitor.app.data.db.entity.MedicationType
import com.diabeticmonitor.app.databinding.DialogAddMedicationBinding
import com.diabeticmonitor.app.ui.medication.MedicationViewModel
import com.diabeticmonitor.app.util.DateTimeUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMedicationDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogAddMedicationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicationViewModel by viewModels({ requireParentFragment() })
    private var scheduledTimestamp = System.currentTimeMillis()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvScheduledTime.text = DateTimeUtils.formatDateTime(scheduledTimestamp)

        binding.btnPickTime.setOnClickListener {
            val (h, m) = DateTimeUtils.millisToHourMinute(scheduledTimestamp)
            TimePickerDialog(requireContext(), { _, hour, minute ->
                scheduledTimestamp = DateTimeUtils.setTimeOnToday(hour, minute)
                binding.tvScheduledTime.text = DateTimeUtils.formatDateTime(scheduledTimestamp)
            }, h, m, false).show()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etMedName.text.toString().trim()
            val dose = binding.etDose.text.toString().trim()
            if (name.isEmpty()) {
                binding.etMedName.error = "Enter medication name"
                return@setOnClickListener
            }
            val type = if (binding.radioInsulin.isChecked) MedicationType.INSULIN else MedicationType.TABLET
            val entry = MedicationEntry(
                medicationType = type.name,
                name = name,
                dose = dose,
                unit = binding.etUnit.text.toString().trim(),
                scheduledTime = scheduledTimestamp,
                notes = binding.etMedNotes.text.toString().trim()
            )
            viewModel.addMedication(entry)
            Toast.makeText(requireContext(), "Medication added!", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
