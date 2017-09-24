package de.schlangguru.paperlessuploader.services.local

import android.content.Context
import de.schlangguru.paperlessuploader.ContextHolder

class Settings (
        context: Context = ContextHolder.appContext
) {

    private val sharedPref by lazy { android.preference.PreferenceManager.getDefaultSharedPreferences(context) }

    val host: String
        get() = sharedPref.getString("pref_con_host", "")

    val connectionConfigured: Boolean
        get() = !host.isEmpty()

    val apiURL: String
        get() {
            if (connectionConfigured) {
                return "http://${host}"
            } else {
                return "http://localhost"
            }
        }

    val username: String
        get() = sharedPref.getString("pref_auth_username", "")

    val password: String
        get() = sharedPref.getString("pref_auth_password", "")

}