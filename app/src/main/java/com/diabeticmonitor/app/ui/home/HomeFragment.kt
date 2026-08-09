package com.diabeticmonitor.app.ui.home

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
import com.diabeticmonitor.app.R
import com.diabeticmonitor.app.data.db.entity.SessionType
import com.diabeticmonitor.app.databinding.FragmentHomeBinding
import com.diabeticmonitor.app.ui.dialog.AddGlucoseDialogFragment
import com.diabeticmonitor.app.util.DateTimeUtils
import com.diabeticmonitor.app.util.GlucoseColorUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSessionCards()
        observeData()
        binding.tvDate.text = DateTimeUtils.formatDate(System.currentTimeMillis())
    }

    private fun setupSessionCards() {
        binding.cardFasting.btnLogSession.setOnClickListener { showAddDialog(SessionType.FASTING) }
        binding.cardAfterBreakfast.btnLogSession.setOnClickListener { showAddDialog(SessionType.AFTER_BREAKFAST) }
        binding.cardAfterLunch.btnLogSession.setOnClickListener { showAddDialog(SessionType.AFTER_LUNCH) }
        binding.cardBeforeDinner.btnLogSession.setOnClickListener { showAddDialog(SessionType.BEFORE_DINNER) }
    }

    private fun showAddDialog(session: SessionType) {
        AddGlucoseDialogFragment.newInstance(session.name)
            .show(childFragmentManager, "add_glucose")
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.todayReadings.collect { readings ->
                        updateSessionCard(SessionType.FASTING, viewModel.getSessionReadings(SessionType.FASTING, readings))
                        updateSessionCard(SessionType.AFTER_BREAKFAST, viewModel.getSessionReadings(SessionType.AFTER_BREAKFAST, readings))
                        updateSessionCard(SessionType.AFTER_LUNCH, viewModel.getSessionReadings(SessionType.AFTER_LUNCH, readings))
                        updateSessionCard(SessionType.BEFORE_DINNER, viewModel.getSessionReadings(SessionType.BEFORE_DINNER, readings))

                        val avg = if (readings.isEmpty()) null else readings.map { it.glucoseLevel }.average().toFloat()
                        binding.tvAvgToday.text = avg?.let { "Today's Avg: ${"%.1f".format(it)} mg/dL" } ?: "No readings today"
                        binding.tvReadingCount.text = "${readings.size} readings logged"
                    }
                }
                launch {
                    viewModel.userProfile.collect { profile ->
                        binding.tvGreeting.text = if (profile?.name?.isNotEmpty() == true)
                            "Hello, ${profile.name}!" else "Hello!"
                    }
                }
            }
        }
    }

    private fun updateSessionCard(session: SessionType, reading: com.diabeticmonitor.app.data.db.entity.GlucoseReading?) {
        val cardBinding = when (session) {
            SessionType.FASTING        -> binding.cardFasting
            SessionType.AFTER_BREAKFAST -> binding.cardAfterBreakfast
            SessionType.AFTER_LUNCH    -> binding.cardAfterLunch
            SessionType.BEFORE_DINNER  -> binding.cardBeforeDinner
        }
        cardBinding.tvSessionName.text = session.displayName
        if (reading != null) {
            cardBinding.tvGlucoseValue.text = "${reading.glucoseLevel.toInt()} mg/dL"
            cardBinding.tvGlucoseStatus.text = GlucoseColorUtils.getLabelForLevel(reading.glucoseLevel)
            cardBinding.tvGlucoseValue.setTextColor(GlucoseColorUtils.getColorForLevel(reading.glucoseLevel))
            cardBinding.tvLastLogged.text = "at ${DateTimeUtils.formatTime(reading.timestamp)}"
            cardBinding.tvLastLogged.isVisible = true
        } else {
            cardBinding.tvGlucoseValue.text = "-- mg/dL"
            cardBinding.tvGlucoseStatus.text = "Not logged"
            cardBinding.tvGlucoseValue.setTextColor(requireContext().getColor(R.color.text_secondary))
            cardBinding.tvLastLogged.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
