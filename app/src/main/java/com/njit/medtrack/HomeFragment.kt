package com.njit.medtrack

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private data class ChecklistDose(
        val name: String,
        val detail: String,
        val taken: Boolean,
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.text_welcome).text =
            getString(R.string.welcome_back, "there")

        val dateFmt = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US)
        view.findViewById<TextView>(R.id.text_date).text = dateFmt.format(Date())

        view.findViewById<TextView>(R.id.text_from_now).text =
            getString(R.string.from_now, "0m")
        view.findViewById<TextView>(R.id.text_next_time).text = "8:00 PM"
        view.findViewById<TextView>(R.id.text_med_name).text = "Metformin"
        view.findViewById<TextView>(R.id.text_med_strength).text = "500mg"

        view.findViewById<TextView>(R.id.text_adherence_percent).text = "67%"
        view.findViewById<TextView>(R.id.text_taken).text =
            getString(R.string.taken_count, 2)
        view.findViewById<TextView>(R.id.text_pending).text =
            getString(R.string.pending_count, 1)

        view.findViewById<MaterialButton>(R.id.btn_mark_taken).setOnClickListener {
            Snackbar.make(it, R.string.mark_as_taken, Snackbar.LENGTH_SHORT).show()
        }

        view.findViewById<TextView>(R.id.text_doses_scheduled).text =
            getString(R.string.doses_scheduled, 3)

        val checklist = view.findViewById<LinearLayout>(R.id.checklist_container)
        val doses = listOf(
            ChecklistDose("Lisinopril", "10mg at 8:00 AM", true),
            ChecklistDose("Metformin", "500mg at 8:00 AM", true),
            ChecklistDose("Metformin", "500mg at 8:00 PM", false),
        )
        val strokePx = resources.getDimensionPixelSize(R.dimen.card_stroke_width)
        doses.forEach { dose ->
            val row = layoutInflater.inflate(R.layout.item_checklist_dose, checklist, false)
            val card = row.findViewById<MaterialCardView>(R.id.card_row)
            val icon = row.findViewById<ImageView>(R.id.icon_status)
            val med = row.findViewById<TextView>(R.id.text_med)
            val detail = row.findViewById<TextView>(R.id.text_detail)
            val badge = row.findViewById<TextView>(R.id.badge_taken)
            val btnMark = row.findViewById<MaterialButton>(R.id.btn_mark_row)
            med.text = dose.name
            detail.text = dose.detail
            if (dose.taken) {
                card.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.taken_green_bg),
                )
                card.strokeWidth = 0
                icon.setImageResource(R.drawable.ic_check_small)
                icon.background = null
                badge.visibility = View.VISIBLE
                btnMark.visibility = View.GONE
            } else {
                card.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.md_surface),
                )
                card.strokeWidth = strokePx
                icon.setImageDrawable(null)
                icon.setBackgroundResource(R.drawable.bg_circle_outline)
                badge.visibility = View.GONE
                btnMark.visibility = View.VISIBLE
                btnMark.setOnClickListener {
                    Snackbar.make(it, R.string.mark_taken, Snackbar.LENGTH_SHORT).show()
                }
            }
            checklist.addView(row)
        }
    }
}
