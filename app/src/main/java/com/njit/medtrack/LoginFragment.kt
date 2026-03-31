package com.njit.medtrack

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.njit.medtrack.auth.AuthRepository
import com.njit.medtrack.auth.toAuthMessage
import com.njit.medtrack.debug.DebugAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val authRepository = AuthRepository()

    private var isLoginMode = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnLoginTab = view.findViewById<MaterialButton>(R.id.btn_tab_login)
        val btnSignupTab = view.findViewById<MaterialButton>(R.id.btn_tab_signup)
        val btnSubmit = view.findViewById<MaterialButton>(R.id.btn_submit)
        val inputEmail = view.findViewById<TextInputEditText>(R.id.input_email)
        val inputPassword = view.findViewById<TextInputEditText>(R.id.input_password)
        val layoutEmail = view.findViewById<TextInputLayout>(R.id.layout_email)
        val layoutPassword = view.findViewById<TextInputLayout>(R.id.layout_password)

        // DEBUG: remove this block when email/password login + SHA setup is ready (see DebugAuth.kt).
        if (DebugAuth.SKIP_LOGIN_FOR_DEBUG) {
            if (DebugAuth.showLoginScreenAfterLogout) {
                DebugAuth.showLoginScreenAfterLogout = false
                // Fall through — show login / sign-up after explicit logout from MainFragment.
            } else {
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                return
            }
        }

        if (Firebase.auth.currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            return
        }

        fun applyTabStyles() {
            val ctx = requireContext()
            val pill = ContextCompat.getDrawable(ctx, R.drawable.bg_tab_pill_white)
            btnLoginTab.backgroundTintList = null
            btnSignupTab.backgroundTintList = null
            if (isLoginMode) {
                btnLoginTab.background = pill
                btnLoginTab.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                btnLoginTab.elevation = 2f * ctx.resources.displayMetrics.density
                btnSignupTab.background = null
                btnSignupTab.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                btnSignupTab.elevation = 0f
                btnSubmit.setText(R.string.login)
            } else {
                btnSignupTab.background = pill
                btnSignupTab.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                btnSignupTab.elevation = 2f * ctx.resources.displayMetrics.density
                btnLoginTab.background = null
                btnLoginTab.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                btnLoginTab.elevation = 0f
                btnSubmit.setText(R.string.sign_up)
            }
        }

        btnLoginTab.setOnClickListener {
            isLoginMode = true
            applyTabStyles()
        }
        btnSignupTab.setOnClickListener {
            isLoginMode = false
            applyTabStyles()
        }
        applyTabStyles()

        btnSubmit.setOnClickListener {
            val email = inputEmail.text?.toString()?.trim().orEmpty()
            val password = inputPassword.text?.toString().orEmpty()
            layoutEmail.error = null
            layoutPassword.error = null
            if (email.isEmpty()) {
                layoutEmail.error = getString(R.string.error_email_required)
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                layoutEmail.error = getString(R.string.error_invalid_email)
                return@setOnClickListener
            }
            if (password.length < 6) {
                layoutPassword.error = getString(R.string.error_password_short)
                return@setOnClickListener
            }
            btnSubmit.isEnabled = false
            val onDone: (Result<Unit>) -> Unit = { result ->
                btnSubmit.isEnabled = true
                result.fold(
                    onSuccess = {
                        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                    },
                    onFailure = { e ->
                        Snackbar.make(
                            view,
                            e.toAuthMessage(getString(R.string.login)),
                            Snackbar.LENGTH_LONG,
                        ).show()
                    },
                )
            }
            if (isLoginMode) {
                authRepository.signIn(email, password, onDone)
            } else {
                authRepository.signUp(email, password, onDone)
            }
        }
    }
}
