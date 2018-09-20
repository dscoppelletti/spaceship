/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.scoppelletti.spaceship.ads.app

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import it.scoppelletti.spaceship.ads.AdsExt
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.consent.ConsentStatus
import it.scoppelletti.spaceship.ads.lifecycle.ConsentState
import it.scoppelletti.spaceship.ads.lifecycle.ConsentViewModel
import it.scoppelletti.spaceship.ads.widget.ConsentPagerAdapter
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.tryFinish
import it.scoppelletti.spaceship.applicationException
import it.scoppelletti.spaceship.inject.Injectable
import kotlinx.android.synthetic.main.it_scoppelletti_ads_consent_activity.*
import mu.KLogger
import mu.KotlinLogging
import javax.inject.Inject

/**
 * Prompts the user for consent to receive perzonalized advertising.
 *
 * @see   it.scoppelletti.spaceship.ads.app.ConsentProgressFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentAgeFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentUnderageFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentPromptFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentPrivacyFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentReminderFragment
 * @since 1.0.0
 *
 * @constructor Sole constructor.
 */
@UiThread
public abstract class AbstractConsentActivity : AppCompatActivity(), Injectable,
        HasSupportFragmentInjector,
        OnDialogResultListener {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector:
            DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var settings: Boolean = false
    private lateinit var viewModel: ConsentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val actionBar: ActionBar

        super.onCreate(savedInstanceState)

        setContentView(R.layout.it_scoppelletti_ads_consent_activity)
        setSupportActionBar(toolbar)
        settings = isSettings()

        if (settings) {
            actionBar = supportActionBar!!
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        contentPager.swipeEnabled = false
        contentPager.adapter = ConsentPagerAdapter(this, supportFragmentManager)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ConsentViewModel::class.java)

        viewModel.state.observe(this, Observer<ConsentState> { state ->
            if (state != null) {
                stateObserver(state)
            }
        })

        viewModel.load()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> =
            fragmentDispatchingAndroidInjector

    private fun stateObserver(state: ConsentState) {
        val prefs: SharedPreferences
        val editor: SharedPreferences.Editor

        state.error?.poll()?.let { err ->
            showExceptionDialog(err)
            return
        }

        if (settings) {
            if (state.data.consentStatus == ConsentStatus.NOT_IN_EEA) {
                showExceptionDialog(applicationException {
                    message(R.string.it_scoppelletti_ads_err_notInEea)
                })

                return
            }
        } else if (state.data.consentStatus != ConsentStatus.UNKNOWN) {
            prefs = PreferenceManager.getDefaultSharedPreferences(this)
            editor = prefs.edit()
            editor.putString(AdsExt.PROP_CONSENT, state.data.consentStatus.name)
            editor.apply()

            onComplete()
            tryFinish()
            return
        }

        contentPager.currentItem = state.step
    }

    /**
     * Called when the user completes her choice.
     */
    public abstract fun onComplete()

    private fun isSettings(): Boolean {
        val mode: Int
        val activityInfo: ActivityInfo

        try {
            activityInfo = packageManager.getActivityInfo(componentName,
                    PackageManager.GET_META_DATA)
        } catch (ex: PackageManager.NameNotFoundException) {
            logger.error(ex) {
                "Failed to get ActivityInfo for activity $componentName."
            }

            return false
        }

        mode = activityInfo.metaData?.getInt(
                AbstractConsentActivity.PROP_SETTINGS, 0) ?: 0
        return (mode != 0)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if (!viewModel.backStep()) {
                    tryFinish()
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (viewModel.backStep()) {
            return
        }

        super.onBackPressed()
    }

    override fun onDialogResult(tag: String, which: Int) {
        when (tag) {
            ExceptionDialogFragment.TAG -> {
               tryFinish()
            }
        }
    }

    public companion object {

        /**
         * Property indicating whether this activity has been launched as a
         * settings activity.
         */
        public const val PROP_SETTINGS: String =
                "it.scoppelletti.spaceship.ads.settings"

        private val logger: KLogger = KotlinLogging.logger {}
    }
}