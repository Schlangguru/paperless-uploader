package de.schlangguru.paperlessuploader.ui.settings


import android.annotation.TargetApi
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.text.TextUtils
import android.view.MenuItem
import de.schlangguru.paperlessuploader.R
import de.schlangguru.paperlessuploader.ui.main.MainView

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
 * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
 * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()

        // Display the fragment as the main content.
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, GeneralPreferenceFragment())
                .commit()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }


    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("pref_con_host"))
            bindPreferenceSummaryToValue(findPreference("pref_auth_username"))
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, MainView::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val index = preference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                        if (index >= 0)
                            preference.entries[index]
                        else
                            null)

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.
         *
         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
    }
}
