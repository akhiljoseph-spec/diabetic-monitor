package com.diabeticmonitor.app.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diabeticmonitor.app.data.db.entity.MedicationEntry
import com.diabeticmonitor.app.databinding.ItemMedicationBinding
import com.diabeticmonitor.app.util.DateTimeUtils

class MedicationAdapter(
    private val onMarkTaken: (MedicationEntry) -> Unit,
    private val onDelete: (MedicationEntry) -> Unit
) : ListAdapter<MedicationEntry, MedicationAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemMedicationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: MedicationEntry) {
            binding.tvMedName.text = entry.name
            binding.tvMedType.text = entry.medicationType
            binding.tvDose.text = "${entry.dose} ${entry.unit}".trim()
            binding.tvScheduledTime.text = "Scheduled: ${DateTimeUtils.formatDateTime(entry.scheduledTime)}"

            if (entry.isTaken) {
                binding.tvMedName.paintFlags = binding.tvMedName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.btnMarkTaken.text = "✓ Taken"
                binding.btnMarkTaken.isEnabled = false
                entry.takenTime?.let {
                    binding.tvScheduledTime.text = "Taken at: ${DateTimeUtils.formatTime(it)}"
                }
            } else {
                binding.tvMedName.paintFlags = binding.tvMedName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.btnMarkTaken.text = "Mark Taken"
                binding.btnMarkTaken.isEnabled = true
                binding.btnMarkTaken.setOnClickListener { onMarkTaken(entry) }
            }
            binding.btnDelete.setOnClickListener { onDelete(entry) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMedicationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<MedicationEntry>() {
        override fun areItemsTheSame(a: MedicationEntry, b: MedicationEntry) = a.id == b.id
        override fun areContentsTheSame(a: MedicationEntry, b: MedicationEntry) = a == b
    }
}
