package com.njit.medtrack

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.njit.medtrack.data.MedicationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyMedsFragment : Fragment(R.layout.fragment_my_meds) {

    private val repo by lazy { MedicationRepository(requireContext().applicationContext) }
    private lateinit var adapter: MedicationAdapter
    private var allItems: List<Medication> = emptyList()
    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_medications)
        val search = view.findViewById<TextInputEditText>(R.id.input_search)
        val addBtn = view.findViewById<MaterialButton>(R.id.btn_add_medication)
        val loading = view.findViewById<CircularProgressIndicator>(R.id.loading_medications)
        val empty = view.findViewById<TextView>(R.id.text_empty_medications)

        adapter = MedicationAdapter(
            onOpen = { med -> Snackbar.make(view, med.name, Snackbar.LENGTH_SHORT).show() },
            onEdit = { med -> Snackbar.make(view, "Edit ${med.name}", Snackbar.LENGTH_SHORT).show() },
            onDelete = { med ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val result = repo.deleteMedication(med.id)
                    result.onFailure {
                        Snackbar.make(view, "Could not delete ${med.name}", Snackbar.LENGTH_SHORT).show()
                    }
                }
            },
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        loading.isVisible = true

        job = viewLifecycleOwner.lifecycleScope.launch {
            repo.watchMedications().collect { result ->
                loading.isVisible = false
                result.onSuccess { meds ->
                    allItems = meds
                    val filtered = applyFilter(search.text?.toString()?.trim()?.lowercase().orEmpty())
                    adapter.submitList(filtered)
                    empty.isVisible = filtered.isEmpty()
                    recycler.isVisible = filtered.isNotEmpty()
                }
                result.onFailure {
                    empty.text = getString(R.string.error_load_medications)
                    empty.isVisible = true
                }
            }
        }

        search.doAfterTextChanged { text ->
            val q = text?.toString()?.trim()?.lowercase().orEmpty()
            val filtered = applyFilter(q)
            adapter.submitList(filtered)
            val loaded = !loading.isVisible
            empty.isVisible = loaded && filtered.isEmpty()
            recycler.isVisible = filtered.isNotEmpty()
        }

        addBtn.setOnClickListener { showAddMedicationDialog(view) }
    }

    private fun showAddMedicationDialog(root: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_medication, null)
        val layoutName = dialogView.findViewById<TextInputLayout>(R.id.layout_med_name)
        val inputName = dialogView.findViewById<TextInputEditText>(R.id.input_med_name)
        val inputDosage = dialogView.findViewById<TextInputEditText>(R.id.input_med_dosage)
        val inputTime = dialogView.findViewById<TextInputEditText>(R.id.input_med_time)
        val inputTags = dialogView.findViewById<TextInputEditText>(R.id.input_med_tags)
        val inputNotes = dialogView.findViewById<TextInputEditText>(R.id.input_med_notes)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_add_medication)
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.action_save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = inputName.text?.toString()?.trim().orEmpty()
                if (name.isEmpty()) {
                    layoutName.error = getString(R.string.error_name_required)
                    return@setOnClickListener
                }
                layoutName.error = null
                val tags = inputTags.text?.toString()
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    ?: emptyList()

                val medication = Medication(
                    name = name,
                    dosage = inputDosage.text?.toString()?.trim().orEmpty(),
                    scheduleTime = inputTime.text?.toString()?.trim().orEmpty(),
                    tags = tags,
                    notes = inputNotes.text?.toString()?.trim().orEmpty(),
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    val result = repo.addMedication(medication)
                    result.onFailure { e ->
                        Log.e("MyMedsFragment", "Could not save medication", e)
                        val msg = e.message ?: e::class.java.simpleName
                        Snackbar.make(root, "Could not save medication: $msg", Snackbar.LENGTH_LONG).show()
                    }
                }
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun applyFilter(q: String): List<Medication> =
        if (q.isEmpty()) allItems
        else allItems.filter {
            it.name.lowercase().contains(q) || it.dosage.lowercase().contains(q)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        job = null
    }
}
