package com.njit.medtrack

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.njit.medtrack.data.UserProfileRepository

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val profileRepository = UserProfileRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = Firebase.auth.currentUser
        val emailFallback = user?.email.orEmpty()
        val nameView = view.findViewById<TextView>(R.id.text_profile_name)
        val emailView = view.findViewById<TextView>(R.id.text_profile_email)
        val uidView = view.findViewById<TextView>(R.id.text_profile_uid)

        nameView.text = emailFallback.substringBefore("@").ifBlank { "—" }
        emailView.text = emailFallback.ifBlank { "—" }
        uidView.text = user?.uid ?: "—"

        profileRepository.fetchProfileForCurrentUser { result ->
            if (!isAdded) return@fetchProfileForCurrentUser
            val profile = result.getOrNull() ?: return@fetchProfileForCurrentUser
            val email = profile.email.ifBlank { emailFallback }
            emailView.text = email.ifBlank { "—" }
            nameView.text = email.substringBefore("@").ifBlank { "—" }
        }
    }
}
