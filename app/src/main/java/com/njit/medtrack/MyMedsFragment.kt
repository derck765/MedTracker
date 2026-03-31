package com.njit.medtrack

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class MyMedsFragment : Fragment(R.layout.fragment_my_meds) {

    private val allItems by lazy { sampleLibrary().toMutableList() }
    private lateinit var adapter: MedicationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_medications)
        val search = view.findViewById<TextInputEditText>(R.id.input_search)
        val addBtn = view.findViewById<MaterialButton>(R.id.btn_add_medication)

        adapter = MedicationAdapter(
            onOpen = { med -> Snackbar.make(view, med.name, Snackbar.LENGTH_SHORT).show() },
            onEdit = { med -> Snackbar.make(view, "Edit ${med.name}", Snackbar.LENGTH_SHORT).show() },
            onDelete = { med -> Snackbar.make(view, "Delete ${med.name}", Snackbar.LENGTH_SHORT).show() },
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter
        adapter.submitList(allItems)

        search.doAfterTextChanged { text ->
            val q = text?.toString()?.trim()?.lowercase().orEmpty()
            val filtered = if (q.isEmpty()) allItems else allItems.filter {
                it.name.lowercase().contains(q) || it.dosage.lowercase().contains(q)
            }
            adapter.submitList(filtered)
        }

        addBtn.setOnClickListener {
            Snackbar.make(it, R.string.add_medication_toast, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun sampleLibrary(): List<Medication> = listOf(
        Medication(
            id = "1",
            name = "Lisinopril",
            dosage = "10mg",
            scheduleTime = "8:00 AM",
            tags = listOf(getString(R.string.daily), "25 pills"),
            notes = "Take with water",
        ),
        Medication(
            id = "2",
            name = "Metformin",
            dosage = "500mg",
            scheduleTime = "8:00 AM · 8:00 PM",
            tags = listOf(getString(R.string.daily)),
            notes = "",
        ),
    )
}
