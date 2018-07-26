package it.scoppelletti.spaceship.preference.sample

import android.os.Bundle
import android.support.v7.preference.Preference
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
            val pref: Preference

            setPreferencesFromResource(R.xml.preferences, rootKey)

            pref = findPreference(MainApp.PROP_FEEDBACK)
            StartActivityPreferenceDecorator(requireActivity(), pref,
                    R.string.it_scoppelletti_pref_cmd_feedback)
        }
    }
}
