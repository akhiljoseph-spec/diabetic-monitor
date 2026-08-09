package com.diabeticmonitor.app.ui.medication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.diabeticmonitor.app.databinding.FragmentMedicationBinding
import com.diabeticmonitor.app.ui.adapters.MedicationAdapter
import com.diabeticmonitor.app.ui.dialog.AddMedicationDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MedicationFragment : Fragment() {

    private var _binding: FragmentMedicationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicationViewModel by viewModels()
    private lateinit var adapter: MedicationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MedicationAdapter(
            onMarkTaken = { entry -> viewModel.markAsTaken(entry.id) },
            onDelete = { entry -> viewModel.delete(entry) }
        )
        binding.recyclerMedications.adapter = adapter
        binding.fabAddMedication.setOnClickListener {
            AddMedicationDialogFragment().show(childFragmentManager, "add_medication")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allMedications.collect { meds ->
                    adapter.submitList(meds)
                    binding.tvEmpty.isVisible = meds.isEmpty()
                    binding.recyclerMedications.isVisible = meds.isNotEmpty()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
