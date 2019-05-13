package it.scoppelletti.spaceship.preference.sample

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import it.scoppelletti.spaceship.html.app.HtmlViewerActivity
import it.scoppelletti.spaceship.preference.AbstractPreferenceFragment
import it.scoppelletti.spaceship.preference.AbstractSettingsActivity
import it.scoppelletti.spaceship.preference.CreditsActivity

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
}

class SettingsFragment : AbstractPreferenceFragment() {

    override fun onCreatePreferences(
            savedInstanceState: Bundle?,
            rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference(MainApp.PROP_HELP).startActivityConfig { intent ->
            intent.putExtra(HtmlViewerActivity.PROP_TITLE,
                    R.string.it_scoppelletti_pref_cmd_help)
            intent.putExtra(HtmlViewerActivity.PROP_HOMEASUP, true)
            intent.putExtra(HtmlViewerActivity.PROP_TEXT, R.string.html_help)
        }

        findPreference(MainApp.PROP_FEEDBACK).startActivityConfig()

        findPreference(MainApp.PROP_CREDITS).startActivityConfig { intent ->
            intent.putExtra(CreditsActivity.PROP_CREDITS, R.xml.credits)
        }
    }
}

