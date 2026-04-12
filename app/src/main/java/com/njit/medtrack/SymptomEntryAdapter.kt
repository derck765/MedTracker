package com.njit.medtrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.njit.medtrack.data.SymptomEntry
import java.text.SimpleDateFormat
import java.util.Locale

class SymptomEntryAdapter : ListAdapter<SymptomEntry, SymptomEntryAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_symptom_entry, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val date: TextView = itemView.findViewById(R.id.text_entry_date)
        private val severity: TextView = itemView.findViewById(R.id.text_entry_severity)
        private val symptoms: TextView = itemView.findViewById(R.id.text_entry_symptoms)
        private val notes: TextView = itemView.findViewById(R.id.text_entry_notes)
        private val fmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

        fun bind(item: SymptomEntry) {
            date.text = fmt.format(item.date)
            severity.text = itemView.context.getString(R.string.severity_label, item.severity)
            symptoms.text = item.symptoms
            notes.text = item.notes
            notes.isVisible = item.notes.isNotBlank()
        }
    }

    private object Diff : DiffUtil.ItemCallback<SymptomEntry>() {
        override fun areItemsTheSame(a: SymptomEntry, b: SymptomEntry) = a.id == b.id
        override fun areContentsTheSame(a: SymptomEntry, b: SymptomEntry) = a == b
    }
}
