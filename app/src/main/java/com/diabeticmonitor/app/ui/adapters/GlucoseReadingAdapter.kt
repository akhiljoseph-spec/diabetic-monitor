package com.diabeticmonitor.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diabeticmonitor.app.data.db.entity.GlucoseReading
import com.diabeticmonitor.app.databinding.ItemGlucoseReadingBinding
import com.diabeticmonitor.app.util.DateTimeUtils
import com.diabeticmonitor.app.util.GlucoseColorUtils

class GlucoseReadingAdapter(
    private val onDelete: (GlucoseReading) -> Unit
) : ListAdapter<GlucoseReading, GlucoseReadingAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemGlucoseReadingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reading: GlucoseReading) {
            binding.tvDateTime.text = DateTimeUtils.formatDateTime(reading.timestamp)
            binding.tvSession.text = reading.sessionType.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercase() }
            binding.tvGlucoseLevel.text = "${reading.glucoseLevel.toInt()} ${reading.unit}"
            binding.tvStatus.text = GlucoseColorUtils.getLabelForLevel(reading.glucoseLevel)
            val color = GlucoseColorUtils.getColorForLevel(reading.glucoseLevel)
            binding.tvGlucoseLevel.setTextColor(color)
            binding.tvStatus.setTextColor(color)
            binding.viewColorIndicator.setBackgroundColor(color)
            if (reading.notes.isNotEmpty()) {
                binding.tvNotes.text = reading.notes
                binding.tvNotes.visibility = android.view.View.VISIBLE
            } else {
                binding.tvNotes.visibility = android.view.View.GONE
            }
            binding.btnDelete.setOnClickListener { onDelete(reading) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGlucoseReadingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<GlucoseReading>() {
        override fun areItemsTheSame(a: GlucoseReading, b: GlucoseReading) = a.id == b.id
        override fun areContentsTheSame(a: GlucoseReading, b: GlucoseReading) = a == b
    }
}
