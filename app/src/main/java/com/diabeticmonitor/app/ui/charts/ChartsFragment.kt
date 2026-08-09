package com.diabeticmonitor.app.ui.charts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.diabeticmonitor.app.data.db.entity.GlucoseReading
import com.diabeticmonitor.app.databinding.FragmentChartsBinding
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ChartsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChartsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        setupPeriodButtons()
        observeData()
    }

    private fun setupChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setDrawGridBackground(false)
            legend.isEnabled = true
            setNoDataText("No glucose readings available")

            // Reference lines
            val minLine = LimitLine(70f, "Min (70)").apply {
                lineWidth = 2f
                lineColor = Color.parseColor("#FF9800")
                textColor = Color.parseColor("#FF9800")
                textSize = 10f
            }
            val maxLine = LimitLine(140f, "Max (140)").apply {
                lineWidth = 2f
                lineColor = Color.parseColor("#F44336")
                textColor = Color.parseColor("#F44336")
                textSize = 10f
            }
            axisLeft.addLimitLine(minLine)
            axisLeft.addLimitLine(maxLine)
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 300f
            axisRight.isEnabled = false
        }
    }

    private fun setupPeriodButtons() {
        binding.btnDay.setOnClickListener { viewModel.setPeriod(ChartPeriod.DAY) }
        binding.btnMonth.setOnClickListener { viewModel.setPeriod(ChartPeriod.MONTH) }
        binding.btnYear.setOnClickListener { viewModel.setPeriod(ChartPeriod.YEAR) }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.readings.collect { readings ->
                    updateChart(readings)
                    updateStats(readings)
                }
            }
        }
    }

    private fun updateChart(readings: List<GlucoseReading>) {
        if (readings.isEmpty()) {
            binding.lineChart.clear()
            binding.lineChart.invalidate()
            return
        }

        val sorted = readings.sortedBy { it.timestamp }
        val entries = sorted.mapIndexed { idx, r ->
            Entry(idx.toFloat(), r.glucoseLevel)
        }

        val dataSet = LineDataSet(entries, "Glucose (mg/dL)").apply {
            color = Color.parseColor("#1565C0")
            valueTextColor = Color.BLACK
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(Color.parseColor("#1565C0"))
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
            setDrawFilled(true)
            fillColor = Color.parseColor("#1565C0")
            fillAlpha = 30

            // Color individual circles by status
            val circleColors = sorted.map { r ->
                when {
                    r.glucoseLevel < 70f  -> Color.parseColor("#FF9800")
                    r.glucoseLevel > 140f -> Color.parseColor("#F44336")
                    else                  -> Color.parseColor("#4CAF50")
                }
            }
            setCircleColors(circleColors)
        }

        val timeFormatter = when (viewModel.getCurrentPeriod()) {
            ChartPeriod.DAY   -> SimpleDateFormat("HH:mm", Locale.getDefault())
            ChartPeriod.MONTH -> SimpleDateFormat("dd MMM", Locale.getDefault())
            ChartPeriod.YEAR  -> SimpleDateFormat("MMM", Locale.getDefault())
        }

        binding.lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val idx = value.toInt().coerceIn(0, sorted.size - 1)
                return timeFormatter.format(Date(sorted[idx].timestamp))
            }
        }

        binding.lineChart.data = LineData(dataSet)
        binding.lineChart.invalidate()
    }

    private fun updateStats(readings: List<GlucoseReading>) {
        if (readings.isEmpty()) {
            binding.tvStatAvg.text = "Avg: --"
            binding.tvStatMin.text = "Min: --"
            binding.tvStatMax.text = "Max: --"
            return
        }
        val avg = readings.map { it.glucoseLevel }.average()
        binding.tvStatAvg.text = "Avg: ${"%.1f".format(avg)}"
        binding.tvStatMin.text = "Min: ${"%.1f".format(readings.minOf { it.glucoseLevel })}"
        binding.tvStatMax.text = "Max: ${"%.1f".format(readings.maxOf { it.glucoseLevel })}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
