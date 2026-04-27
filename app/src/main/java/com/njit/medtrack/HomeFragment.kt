package com.njit.medtrack

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.njit.medtrack.data.DoseLogRepository
import com.njit.medtrack.data.MedicationRepository
import com.njit.medtrack.api.InteractionChecker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val medsRepo by lazy { MedicationRepository(requireContext().applicationContext) }
    private val doseRepo by lazy { DoseLogRepository(requireContext().applicationContext) }

    private data class DoseSlot(
        val medicationId: Long,
        val name: String,
        val dosage: String,
        val scheduleText: String,
        val scheduledFor: Long,
        val taken: Boolean,
    )

    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.text_welcome).text =
            getString(R.string.welcome_back, "there")

        val dateFmt = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US)
        view.findViewById<TextView>(R.id.text_date).text = dateFmt.format(Date())
        val button = view.findViewById<MaterialButton>(R.id.btn_test_interaction)

        button.setOnClickListener {
            checkInteractions(view)
        }

        job = viewLifecycleOwner.lifecycleScope.launch {
            combine(
                medsRepo.watchMedications().map { it.getOrDefault(emptyList()) },
                doseRepo.watchTodaysLogs(),
            ) { meds, logs ->
                val loggedSlots = logs.map { it.medicationId to it.scheduledFor }.toSet()
                meds.map { med ->
                    val slot = parseSlotMillis(med.scheduleTime)
                    DoseSlot(
                        medicationId = med.id,
                        name = med.name,
                        dosage = med.dosage,
                        scheduleText = med.scheduleTime,
                        scheduledFor = slot,
                        taken = (med.id to slot) in loggedSlots,
                    )
                }.sortedBy { it.scheduledFor }
            }.collect { slots ->
                renderChecklist(view, slots)
                renderNextDose(view, slots)
                renderAdherence(view, slots)
            }
        }
    }
    private fun showResultDialog(result: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Medication Interactions")
            .setMessage(result)
            .setPositiveButton("OK", null)
            .show()
    }
    private fun checkInteractions(root: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val meds = medsRepo.getAllMedications()

                if (meds.size < 2) {
                    Snackbar.make(root, "Add at least two medications", Snackbar.LENGTH_LONG).show()
                    return@launch
                }

                val checker = InteractionChecker(BuildConfig.GEMINI_API_KEY)

                val result = checker.checkInteractions(meds)

                showResultDialog(result)

            } catch (e: Exception) {
                Snackbar.make(root, "Error: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        }
    }
    private fun renderChecklist(root: View, slots: List<DoseSlot>) {
        val checklist = root.findViewById<LinearLayout>(R.id.checklist_container)
        checklist.removeAllViews()
        root.findViewById<TextView>(R.id.text_doses_scheduled).text =
            getString(R.string.doses_scheduled, slots.size)
        val strokePx = resources.getDimensionPixelSize(R.dimen.card_stroke_width)
        slots.forEach { slot ->
            val row = layoutInflater.inflate(R.layout.item_checklist_dose, checklist, false)
            val card = row.findViewById<MaterialCardView>(R.id.card_row)
            val icon = row.findViewById<ImageView>(R.id.icon_status)
            val med = row.findViewById<TextView>(R.id.text_med)
            val detail = row.findViewById<TextView>(R.id.text_detail)
            val badge = row.findViewById<TextView>(R.id.badge_taken)
            val btnMark = row.findViewById<MaterialButton>(R.id.btn_mark_row)
            med.text = slot.name
            detail.text = buildDetail(slot)
            if (slot.taken) {
                card.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.taken_green_bg),
                )
                card.strokeWidth = 0
                icon.setImageResource(R.drawable.ic_check_small)
                icon.background = null
                badge.visibility = View.VISIBLE
                btnMark.visibility = View.GONE
                btnMark.setOnClickListener(null)
            } else {
                card.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.md_surface),
                )
                card.strokeWidth = strokePx
                icon.setImageDrawable(null)
                icon.setBackgroundResource(R.drawable.bg_circle_outline)
                badge.visibility = View.GONE
                btnMark.visibility = View.VISIBLE
                btnMark.setOnClickListener { markTaken(slot) }
            }
            checklist.addView(row)
        }
    }

    private fun renderNextDose(root: View, slots: List<DoseSlot>) {
        val next = slots.firstOrNull { !it.taken }
        root.findViewById<TextView>(R.id.text_next_time).text =
            next?.scheduleText?.takeIf { it.isNotBlank() } ?: "--"
        root.findViewById<TextView>(R.id.text_med_name).text =
            next?.name ?: "No upcoming doses"
        root.findViewById<TextView>(R.id.text_med_strength).text = next?.dosage.orEmpty()
        root.findViewById<TextView>(R.id.text_from_now).text = formatFromNow(next?.scheduledFor)

        val heroBtn = root.findViewById<MaterialButton>(R.id.btn_mark_taken)
        if (next == null) {
            heroBtn.isEnabled = false
            heroBtn.setOnClickListener(null)
        } else {
            heroBtn.isEnabled = true
            heroBtn.setOnClickListener { markTaken(next) }
        }
    }

    private fun renderAdherence(root: View, slots: List<DoseSlot>) {
        val total = slots.size
        val taken = slots.count { it.taken }
        val pending = total - taken
        val percent = if (total == 0) 0 else ((taken * 100f) / total).roundToInt()
        root.findViewById<TextView>(R.id.text_adherence_percent).text = "$percent%"
        root.findViewById<TextView>(R.id.text_taken).text =
            getString(R.string.taken_count, taken)
        root.findViewById<TextView>(R.id.text_pending).text =
            getString(R.string.pending_count, pending)
        root.findViewById<ProgressBar>(R.id.progress_adherence).progress = percent
    }

    private fun markTaken(slot: DoseSlot) {
        val root = view ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            val result = doseRepo.markTaken(slot.medicationId, slot.scheduledFor)
            result.onFailure {
                Snackbar.make(root, "Could not mark ${slot.name} as taken", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildDetail(slot: DoseSlot): String {
        val dosage = slot.dosage.trim()
        val time = slot.scheduleText.trim()
        return when {
            dosage.isNotEmpty() && time.isNotEmpty() -> "$dosage at $time"
            time.isNotEmpty() -> time
            else -> dosage
        }
    }

    private fun parseSlotMillis(scheduleTime: String): Long {
        val trimmed = scheduleTime.trim()
        if (trimmed.isNotEmpty()) {
            val patterns = listOf("h:mm a", "h:mma", "H:mm")
            for (p in patterns) {
                try {
                    val fmt = SimpleDateFormat(p, Locale.US).apply { isLenient = false }
                    val parsed = fmt.parse(trimmed) ?: continue
                    val parsedCal = Calendar.getInstance().apply { time = parsed }
                    return todayAt(
                        parsedCal.get(Calendar.HOUR_OF_DAY),
                        parsedCal.get(Calendar.MINUTE),
                    )
                } catch (_: Exception) {
                    // try next pattern
                }
            }
        }
        return todayAt(0, 0)
    }

    private fun todayAt(hour: Int, minute: Int): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun formatFromNow(scheduledFor: Long?): String {
        if (scheduledFor == null) return ""
        val diffMin = (scheduledFor - System.currentTimeMillis()) / 60_000
        if (diffMin <= 0L) return ""
        val hours = diffMin / 60
        val mins = diffMin % 60
        val label = when {
            hours > 0 && mins > 0 -> "${hours}h ${mins}m"
            hours > 0 -> "${hours}h"
            else -> "${mins}m"
        }
        return getString(R.string.from_now, label)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        job = null
    }
}
