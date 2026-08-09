package com.diabeticmonitor.app.ui.history

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
import com.diabeticmonitor.app.databinding.FragmentHistoryBinding
import com.diabeticmonitor.app.ui.adapters.GlucoseReadingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: GlucoseReadingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = GlucoseReadingAdapter { reading ->
            viewModel.delete(reading)
        }
        binding.recyclerHistory.adapter = adapter

        binding.chipAll.setOnClickListener { viewModel.setFilter(HistoryFilter.ALL) }
        binding.chipToday.setOnClickListener { viewModel.setFilter(HistoryFilter.TODAY) }
        binding.chipWeek.setOnClickListener { viewModel.setFilter(HistoryFilter.WEEK) }
        binding.chipMonth.setOnClickListener { viewModel.setFilter(HistoryFilter.MONTH) }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.readings.collect { readings ->
                    adapter.submitList(readings)
                    binding.tvEmpty.isVisible = readings.isEmpty()
                    binding.recyclerHistory.isVisible = readings.isNotEmpty()
                    binding.tvCount.text = "${readings.size} readings"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
