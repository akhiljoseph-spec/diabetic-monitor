package com.diabeticmonitor.app.ui.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.diabeticmonitor.app.data.db.entity.GlucoseReading
import com.diabeticmonitor.app.data.db.entity.SessionType
import com.diabeticmonitor.app.databinding.DialogAddGlucoseBinding
import com.diabeticmonitor.app.ui.home.HomeViewModel
import com.diabeticmonitor.app.util.DateTimeUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AddGlucoseDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogAddGlucoseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })
    private var selectedTimestamp = System.currentTimeMillis()
    private var sessionType = SessionType.FASTING

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddGlucoseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString(ARG_SESSION)?.let { name ->
            sessionType = SessionType.valueOf(name)
        }

        binding.tvSessionLabel.text = sessionType.displayName
        binding.tvTimestamp.text = DateTimeUtils.formatDateTime(selectedTimestamp)

        binding.btnPickTime.setOnClickListener {
            val (h, m) = DateTimeUtils.millisToHourMinute(selectedTimestamp)
            TimePickerDialog(requireContext(), { _, hour, minute ->
                selectedTimestamp = DateTimeUtils.setTimeOnToday(hour, minute)
                binding.tvTimestamp.text = DateTimeUtils.formatDateTime(selectedTimestamp)
            }, h, m, false).show()
        }

        binding.btnSave.setOnClickListener {
            val raw = binding.etGlucoseLevel.text.toString().trim()
            val level = raw.toFloatOrNull()
            if (level == null || level <= 0) {
                binding.etGlucoseLevel.error = "Enter a valid glucose level"
                return@setOnClickListener
            }
            val reading = GlucoseReading(
                sessionType = sessionType.name,
                glucoseLevel = level,
                timestamp = selectedTimestamp,
                notes = binding.etNotes.text.toString().trim()
            )
            viewModel.addReading(reading)
            Toast.makeText(requireContext(), "Reading saved!", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SESSION = "session_type"

        fun newInstance(sessionType: String) = AddGlucoseDialogFragment().apply {
            arguments = Bundle().apply { putString(ARG_SESSION, sessionType) }
        }
    }
}
