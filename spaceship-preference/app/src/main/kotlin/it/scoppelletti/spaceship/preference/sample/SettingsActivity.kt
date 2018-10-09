package it.scoppelletti.spaceship.preference.sample

import android.os.Bundle
import it.scoppelletti.spaceship.html.HtmlExt
import it.scoppelletti.spaceship.preference.AbstractPreferenceFragment
import it.scoppelletti.spaceship.preference.AbstractSettingsActivity
import it.scoppelletti.spaceship.preference.StartActivityPreferenceDecorator

class SettingsActivity : AbstractSettingsActivity() {

    override fun createFragment(): AbstractPreferenceFragment =
            SettingsFragment()

    class SettingsFragment : AbstractPreferenceFragment() {

        override fun onCreatePreferences(
                savedInstanceState: Bundle?,
                rootKey: String?
        ) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            StartActivityPreferenceDecorator(requireActivity(),
                    findPreference(MainApp.PROP_HELP)) { intent ->
                intent.putExtra(HtmlExt.PROP_TITLE,
                        R.string.it_scoppelletti_pref_cmd_help)
                intent.putExtra(HtmlExt.PROP_HOMEASUP, true)
                intent.putExtra(HtmlExt.PROP_TEXT, R.string.html_help)
            }

            StartActivityPreferenceDecorator(requireActivity(),
                    findPreference(MainApp.PROP_FEEDBACK))
        }
    }
}
