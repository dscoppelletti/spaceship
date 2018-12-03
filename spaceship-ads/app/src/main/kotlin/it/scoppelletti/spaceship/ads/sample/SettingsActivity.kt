package it.scoppelletti.spaceship.ads.sample

import android.os.Bundle
import it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
import it.scoppelletti.spaceship.preference.AbstractPreferenceFragment
import it.scoppelletti.spaceship.preference.AbstractSettingsActivity
import it.scoppelletti.spaceship.preference.StartActivityPreferenceDecorator

class SettingsActivity : AbstractSettingsActivity() {

    override fun createFragment(): AbstractPreferenceFragment =
            SettingsFragment()
}

class SettingsFragment : AbstractPreferenceFragment() {

    override fun onCreatePreferences(
            savedInstanceState: Bundle?,
            rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        StartActivityPreferenceDecorator(requireActivity(),
                findPreference(MainApp.PROP_ADS)) { intent ->
            intent.putExtra(AbstractConsentActivity.PROP_SETTINGS, true)
        }
    }
}
