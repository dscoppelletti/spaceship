package it.scoppelletti.spaceship.ads.sample

import android.os.Bundle
import it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
import it.scoppelletti.spaceship.preference.AbstractPreferenceFragment
import it.scoppelletti.spaceship.preference.AbstractSettingsActivity

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

        findPreference(MainApp.PROP_ADS).startActivityConfig { intent ->
            intent.putExtra(AbstractConsentActivity.PROP_SETTINGS, true)
        }
    }
}
