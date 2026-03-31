package com.njit.medtrack

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.njit.medtrack.debug.DebugAuth

class MainFragment : Fragment(R.layout.fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navHost = childFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        bottomNav.setupWithNavController(navHost.navController)

        view.findViewById<View>(R.id.button_logout).setOnClickListener {
            Firebase.auth.signOut()
            DebugAuth.showLoginScreenAfterLogout = true
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }
    }
}
