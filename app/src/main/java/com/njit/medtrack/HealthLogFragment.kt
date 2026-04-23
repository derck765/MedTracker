package com.njit.medtrack

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.njit.medtrack.data.HealthLogRepository
import com.njit.medtrack.data.SymptomEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

class HealthLogFragment : Fragment(R.layout.fragment_health_log) {

    private val repo by lazy { HealthLogRepository(requireContext().applicationContext) }
    private lateinit var adapter: SymptomEntryAdapter
    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val totalEntries = view.findViewById<TextView>(R.id.text_total_entries)
        val avgSeverity = view.findViewById<TextView>(R.id.text_avg_severity)
        val newEntryBtn = view.findViewById<MaterialButton>(R.id.btn_new_entry)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_entries)
        val loading = view.findViewById<CircularProgressIndicator>(R.id.loading_entries)
        val empty = view.findViewById<TextView>(R.id.text_empty_entries)

        adapter = SymptomEntryAdapter(
            onEdit = { entry -> showEditEntryDialog(view, entry) },
            onDelete = { entry ->
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(getString(R.string.confirm_delete_entry))
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.action_delete) { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            repo.deleteEntry(entry.id).onFailure {
                                Snackbar.make(view, "Could not delete entry", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .show()
            },
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        loading.isVisible = true

        job = viewLifecycleOwner.lifecycleScope.launch {
            repo.watchEntries().collect { result ->
                loading.isVisible = false
                result.onSuccess { entries ->
                    totalEntries.text = entries.size.toString()
                    val avg = if (entries.isEmpty()) 0.0 else entries.map { it.severity }.average()
                    avgSeverity.text = String.format(Locale.getDefault(), "%.1f", avg)

                    adapter.submitList(entries)
                    empty.isVisible = entries.isEmpty()
                    recycler.isVisible = entries.isNotEmpty()
                }
                result.onFailure {
                    totalEntries.text = "—"
                    avgSeverity.text = "—"
                    empty.text = getString(R.string.error_load_entries)
                    empty.isVisible = true
                }
            }
        }

        newEntryBtn.setOnClickListener { showAddEntryDialog(view) }
    }
    private fun showAddEntryDialog(root: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_entry, null)
        val layoutSymptoms = dialogView.findViewById<TextInputLayout>(R.id.layout_symptoms)
        val inputSymptoms = dialogView.findViewById<TextInputEditText>(R.id.input_symptoms)
        val severityLabel = dialogView.findViewById<TextView>(R.id.text_severity_value)
        val slider = dialogView.findViewById<Slider>(R.id.slider_severity)
        val inputNotes = dialogView.findViewById<TextInputEditText>(R.id.input_entry_notes)

        slider.stepSize = 1f
        slider.addOnChangeListener { _, value, _ ->
            severityLabel.text = getString(R.string.severity_value_format, value.toInt())
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_add_entry)
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.action_save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val symptoms = inputSymptoms.text?.toString()?.trim().orEmpty()
                if (symptoms.isEmpty()) {
                    layoutSymptoms.error = getString(R.string.error_symptoms_required)
                    return@setOnClickListener
                }
                layoutSymptoms.error = null

                val entry = SymptomEntry(
                    date = Date(),
                    symptoms = symptoms,
                    severity = slider.value.toInt(),
                    notes = inputNotes.text?.toString()?.trim().orEmpty(),
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    val result = repo.addEntry(entry)
                    result.onFailure {
                        Snackbar.make(root, "Could not save entry", Snackbar.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showEditEntryDialog(root: View, existing: SymptomEntry) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_entry, null)
        val layoutSymptoms = dialogView.findViewById<TextInputLayout>(R.id.layout_symptoms)
        val inputSymptoms = dialogView.findViewById<TextInputEditText>(R.id.input_symptoms)
        val severityLabel = dialogView.findViewById<TextView>(R.id.text_severity_value)
        val slider = dialogView.findViewById<Slider>(R.id.slider_severity)
        val inputNotes = dialogView.findViewById<TextInputEditText>(R.id.input_entry_notes)

        inputSymptoms.setText(existing.symptoms)
        slider.stepSize = 1f
        slider.value = existing.severity.toFloat()
        severityLabel.text = getString(R.string.severity_value_format, existing.severity)
        inputNotes.setText(existing.notes)

        slider.addOnChangeListener { _, value, _ ->
            severityLabel.text = getString(R.string.severity_value_format, value.toInt())
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_edit_entry)
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.action_save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val symptoms = inputSymptoms.text?.toString()?.trim().orEmpty()
                if (symptoms.isEmpty()) {
                    layoutSymptoms.error = getString(R.string.error_symptoms_required)
                    return@setOnClickListener
                }
                layoutSymptoms.error = null

                val updated = existing.copy(
                    symptoms = symptoms,
                    severity = slider.value.toInt(),
                    notes = inputNotes.text?.toString()?.trim().orEmpty(),
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    repo.updateEntry(updated).onFailure {
                        Snackbar.make(root, "Could not update entry", Snackbar.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        job = null
    }
}
