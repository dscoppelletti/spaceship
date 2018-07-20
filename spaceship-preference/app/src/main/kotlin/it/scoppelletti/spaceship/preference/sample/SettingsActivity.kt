package it.scoppelletti.spaceship.preference.sample

import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import it.scoppelletti.spaceship.preference.SettingsActivityBase
import it.scoppelletti.spaceship.preference.StartActivityPreferenceDecorator

class SettingsActivity : SettingsActivityBase() {
    override fun createFragment(): PreferenceFragmentCompat {
        return SettingsFragment()
    }

    class SettingsFragment : PreferenceFragmentCompat() {

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
