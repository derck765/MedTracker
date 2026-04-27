package com.njit.medtrack

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MedicationAdapter(
    private val onOpen: (Medication) -> Unit,
    private val onEdit: (Medication) -> Unit,
    private val onDelete: (Medication) -> Unit,
) : ListAdapter<Medication, MedicationAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medication_library, parent, false)
        return VH(view, onOpen, onEdit, onDelete)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        itemView: View,
        private val onOpen: (Medication) -> Unit,
        private val onEdit: (Medication) -> Unit,
        private val onDelete: (Medication) -> Unit,
    ) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView = itemView.findViewById(R.id.text_name)
        private val dosage: TextView = itemView.findViewById(R.id.text_dosage)
        private val normalized: TextView = itemView.findViewById(R.id.text_normalized)
        private val scheduleTime: TextView = itemView.findViewById(R.id.text_schedule_time)
        private val notes: TextView = itemView.findViewById(R.id.text_notes)
        private val rowTags: LinearLayout = itemView.findViewById(R.id.row_tags)
        private val edit: ImageButton = itemView.findViewById(R.id.btn_edit)
        private val delete: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(item: Medication) {
            name.text = item.name
            dosage.text = item.dosage
            scheduleTime.text = item.scheduleTime
            notes.text = item.notes
            notes.isVisible = item.notes.isNotBlank()

            val generic = item.normalizedName
            val rxcui = item.rxcui

            if (!generic.isNullOrEmpty() && generic.lowercase() != item.name.lowercase()) {
                normalized.visibility = View.VISIBLE
                normalized.text = "$generic - RxCUI: $rxcui"
            } else if (!rxcui.isNullOrEmpty()) {
                normalized.visibility = View.VISIBLE
                normalized.text = "RxCUI: $rxcui"
            } else {
                normalized.visibility = View.VISIBLE
                normalized.text = "Unknown RxCUI"
            }

            rowTags.removeAllViews()
            val ctx = itemView.context
            val padH = (10 * ctx.resources.displayMetrics.density).toInt()
            val padV = (4 * ctx.resources.displayMetrics.density).toInt()
            item.tags.forEach { tag ->
                val chip = TextView(ctx).apply {
                    text = tag
                    setPadding(padH, padV, padH, padV)
                    setBackgroundResource(R.drawable.bg_tag_gray)
                    setTextColor(ContextCompat.getColor(ctx, R.color.md_on_surface_variant))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                }
                rowTags.addView(chip, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                ).apply {
                    marginEnd = (8 * ctx.resources.displayMetrics.density).toInt()
                })
            }

            itemView.setOnClickListener { onOpen(item) }
            edit.setOnClickListener { onEdit(item) }
            delete.setOnClickListener { onDelete(item) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Medication>() {
        override fun areItemsTheSame(a: Medication, b: Medication) = a.id == b.id
        override fun areContentsTheSame(a: Medication, b: Medication) = a == b
    }
}
