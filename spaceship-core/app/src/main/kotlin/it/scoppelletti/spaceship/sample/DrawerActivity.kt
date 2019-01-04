package it.scoppelletti.spaceship.sample

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import it.scoppelletti.spaceship.app.NavigationDrawer
import it.scoppelletti.spaceship.app.TitleAdapter
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.applicationException
import it.scoppelletti.spaceship.inject.Injectable
import kotlinx.android.synthetic.main.drawer_activity.*
import java.lang.Exception
import javax.inject.Inject

class DrawerActivity : AppCompatActivity(),
        Injectable,
        HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector:
            DispatchingAndroidInjector<Fragment>

    private lateinit var drawer: NavigationDrawer
    private lateinit var titleAdapter: TitleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_activity)

        drawer = NavigationDrawer(this, drawerLayout, navigationView, toolbar)
        setSupportActionBar(toolbar)
        drawer.onCreate(savedInstanceState)
        titleAdapter = TitleAdapter(this, toolbarLayout)

        fab.hide()
        fab.setOnClickListener {
            val fragment: Fragment?

            fragment = supportFragmentManager.findFragmentById(
                    R.id.contentFrame)
            if (fragment is OnAddClickListener) {
                fragment.onAddClick()
            }
        }

        navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            navigateToFragment(it.itemId)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        drawer.onPostCreate(savedInstanceState)
        titleAdapter.onPostCreate(savedInstanceState)

        val fragment = supportFragmentManager.findFragmentById(
                R.id.contentFrame)
        if (fragment != null) {
            setFragment(fragment)
        } else if (navigateToFragment(R.id.cmd_section1)) {
            navigationView.setCheckedItem(R.id.cmd_section1)
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> =
            fragmentDispatchingAndroidInjector

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        titleAdapter.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (drawer.onBackPressed()) {
            return
        }

        super.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawer.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent: Intent

        if (drawer.onOptionItemSelected(item)) {
            return true
        }

        when (item?.itemId) {
            R.id.cmd_exceptionTest -> {
                onExceptionTest()
                return true
            }

            R.id.cmd_googleApi -> {
                intent = Intent(this, GoogleApiActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun navigateToFragment(itemId: Int): Boolean {
        val fragment: Fragment

        when (itemId) {
            R.id.cmd_section1 -> {
                fragment = ListFragment.newInstance()
            }

            R.id.cmd_section2 -> {
                fragment = StubFragment.newInstance(R.string.cmd_section2)
            }

            R.id.cmd_section3 -> {
                fragment = StubFragment.newInstance(R.string.cmd_section3)
            }

            else -> return false
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

        if (fragment is OnAddClickListener) {
            fab.show()
        } else {
            fab.hide()
        }
    }

    private fun onExceptionTest() {
        val ex: Exception

        ex = applicationException {
            message(R.string.msg_exceptionTest) {
                arguments {
                    add(1)
                }
            }
            cause = RuntimeException("Runtime message.")
        }

        showExceptionDialog(ex) {
            title(R.string.cmd_exceptionTest)
        }
    }
}