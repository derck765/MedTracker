package com.njit.medtrack

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class HealthLogFragment : Fragment(R.layout.fragment_health_log) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.text_total_entries).text = "1"
        view.findViewById<TextView>(R.id.text_avg_severity).text = "2.0"
        view.findViewById<MaterialButton>(R.id.btn_new_entry).setOnClickListener {
            Snackbar.make(it, R.string.new_entry, Snackbar.LENGTH_SHORT).show()
        }
    }
}
