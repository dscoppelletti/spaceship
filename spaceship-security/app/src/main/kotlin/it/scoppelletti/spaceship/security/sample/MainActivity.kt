package it.scoppelletti.spaceship.security.sample

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import it.scoppelletti.spaceship.app.NavigationDrawer
import it.scoppelletti.spaceship.app.TitleAdapter
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.uiComponent
import it.scoppelletti.spaceship.security.sample.lifecycle.MainState
import it.scoppelletti.spaceship.security.sample.lifecycle.MainViewModel
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var drawer: NavigationDrawer
    private lateinit var titleAdapter: TitleAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        drawer = NavigationDrawer(this, drawerLayout, navigationView, toolbar)
        setSupportActionBar(toolbar)
        drawer.onCreate(savedInstanceState)
        titleAdapter = TitleAdapter(this, toolbarLayout)

        navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            navigateToFragment(it.itemId)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        drawer.onPostCreate(savedInstanceState)

        val fragment = supportFragmentManager.findFragmentById(
                R.id.contentFrame)
        if (fragment != null) {
            setFragment(fragment)
        } else if (navigateToFragment(R.id.cmd_key)) {
            navigationView.setCheckedItem(R.id.cmd_key)
        }

        viewModelFactory = uiComponent().viewModelFactory()
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MainViewModel::class.java)

        viewModel.state.observe(this, Observer<MainState> { state ->
            if (state != null) {
                stateObserver(state)
            }
        })
    }

    override fun onBackPressed() {
        if (drawer.onBackPressed()) {
            return
        }

        super.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawer.onConfigurationChanged(newConfig)
    }

    private fun stateObserver(state: MainState) {
        if (state.waiting) {
            progressIndicator.show()
            return
        }

        progressIndicator.hide()

        state.messageId?.poll()?.let { messageId ->
            Snackbar.make(contentFrame, messageId, Snackbar.LENGTH_SHORT).show()
        }

        state.error?.poll()?.let { err ->
            showExceptionDialog(err)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (drawer.onOptionItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun navigateToFragment(itemId: Int): Boolean {
        val fragment: Fragment

        when (itemId) {
            R.id.cmd_key -> {
                fragment = KeyFragment.newInstance()
            }

            R.id.cmd_cipher -> {
                fragment = CipherFragment.newInstance()
            }

            R.id.cmd_providers -> {
                fragment = ProviderFragment.newInstance()
            }

            else -> {
                return false
            }
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .commit()
        setFragment(fragment)

        return true
    }

    private fun setFragment(fragment: Fragment) {
        if (fragment is DrawerFragment) {
            titleAdapter.titleId = fragment.titleId
        }
    }
}
