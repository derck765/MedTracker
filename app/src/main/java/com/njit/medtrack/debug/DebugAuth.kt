package com.njit.medtrack.debug

/**
 * Temporary debug bypass for the login screen.
 *
 * **To remove:** set [SKIP_LOGIN_FOR_DEBUG] to `false`, or delete this file and the
 * `DebugAuth` usages in [com.njit.medtrack.LoginFragment] and [com.njit.medtrack.MainFragment].
 */
object DebugAuth {
    const val SKIP_LOGIN_FOR_DEBUG = true

    /**
     * Set by logout so [com.njit.medtrack.LoginFragment] shows the real login UI even when
     * [SKIP_LOGIN_FOR_DEBUG] is on. Cleared when that screen is shown.
     */
    var showLoginScreenAfterLogout: Boolean = false
}
