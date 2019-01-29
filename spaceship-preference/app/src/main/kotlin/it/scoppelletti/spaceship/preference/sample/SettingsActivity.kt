package it.scoppelletti.spaceship.preference.sample

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import it.scoppelletti.spaceship.html.HtmlExt
import it.scoppelletti.spaceship.preference.AbstractPreferenceFragment
import it.scoppelletti.spaceship.preference.AbstractSettingsActivity
import it.scoppelletti.spaceship.preference.CreditsActivity
import it.scoppelletti.spaceship.preference.StartActivityPreferenceDecorator

class SettingsActivity : AbstractSettingsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        OssLicensesMenuActivity.setActivityTitle(getString(R.string.cmd_oss))
    }

    override fun createFragment(): AbstractPreferenceFragment =
            SettingsFragment()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.cmd_oss -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

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

            StartActivityPreferenceDecorator(requireActivity(),
                    findPreference(MainApp.PROP_CREDITS)) { intent ->
                intent.putExtra(CreditsActivity.PROP_CREDITS, R.xml.credits)
            }
        }
    }
}
