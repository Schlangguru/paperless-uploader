package de.schlangguru.paperlessuploader.services.local

import android.content.Context
import de.schlangguru.paperlessuploader.ContextHolder

/**
 * Simple access object to get the preferences
 * set in the settings view.
 *
 * Accesses the settings through androids PreferenceManager
 * using the provided [context].
 */
class Settings (
        context: Context = ContextHolder.appContext
) {

    /** The underlaying shared preferences. */
    private val sharedPref by lazy { android.preference.PreferenceManager.getDefaultSharedPreferences(context) }

    /** Paperless host. */
    val host: String
        get() = sharedPref.getString("pref_con_host", "")

    /** True whem the user has configured the host. */
    val connectionConfigured: Boolean
        get() = !host.isEmpty()

    /** The paperless api url based on the configured host. */
    val apiURL: String
        get() {
            if (connectionConfigured) {
                return "http://${host}"
            } else {
                return "http://localhost"
            }
        }

    /** Paperless admin username. */
    val username: String
        get() = sharedPref.getString("pref_auth_username", "")

    /** Paperless admin password. */
    val password: String
        get() = sharedPref.getString("pref_auth_password", "")

}