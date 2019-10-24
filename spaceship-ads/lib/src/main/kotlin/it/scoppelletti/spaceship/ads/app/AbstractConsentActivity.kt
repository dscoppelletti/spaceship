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

@file:Suppress("RedundantVisibilityModifier", "RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.ads.app

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.UiThread
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.ads.AdsExt
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.consent.ConsentStatus
import it.scoppelletti.spaceship.ads.i18n.AdsMessages
import it.scoppelletti.spaceship.ads.lifecycle.ConsentState
import it.scoppelletti.spaceship.ads.lifecycle.ConsentStatusObservable
import it.scoppelletti.spaceship.ads.lifecycle.ConsentViewModel
import it.scoppelletti.spaceship.ads.widget.ConsentPagerAdapter
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.tryFinish
import it.scoppelletti.spaceship.app.uiComponent
import kotlinx.android.synthetic.main.it_scoppelletti_ads_consent_activity.*

/**
 * Prompts the user for consent to receive perzonalized advertising.
 *
 * @see   it.scoppelletti.spaceship.ads.app.ConsentLoadFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentAgeFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentUnderageFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentPromptFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentPrivacyFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentReminderFragment
 * @since 1.0.0
 */
@UiThread
public abstract class AbstractConsentActivity : AppCompatActivity(),
        OnDialogResultListener {

    private var settings: Boolean = false
    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: ConsentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val actionBar: ActionBar

        super.onCreate(savedInstanceState)

        setContentView(R.layout.it_scoppelletti_ads_consent_activity)
        setSupportActionBar(toolbar)
        settings = intent.getBooleanExtra(AbstractConsentActivity.PROP_SETTINGS,
                false)

        if (settings) {
            actionBar = supportActionBar!!
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        contentPager.swipeEnabled = false
        contentPager.adapter = ConsentPagerAdapter(this, supportFragmentManager)

        viewModelFactory = uiComponent().viewModelFactory()
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ConsentViewModel::class.java)

        viewModel.state.observe(this, Observer<ConsentState> { state ->
            if (state != null) {
                stateObserver(state)
            }
        })

        viewModel.load()
    }

    private fun stateObserver(state: ConsentState) {
        if (state.waiting) {
            setCurrentItem(state)
            progressIndicator.show()
        } else {
            progressIndicator.hide()
            onStateUpdate(state)
        }
    }

    private fun setCurrentItem(state: ConsentState) {
        if (contentPager.currentItem != state.step) {
            contentPager.currentItem = state.step
        }
    }

    private fun onStateUpdate(state: ConsentState) {
        state.error?.poll()?.let { err ->
            setCurrentItem(state)
            showExceptionDialog(err)
            return
        }

        if (settings) {
            if (state.data.consentStatus == ConsentStatus.NOT_IN_EEA) {
                setCurrentItem(state)
                showExceptionDialog(ApplicationException(
                        AdsMessages.errorUserNotLocatedInEea()))
                return
            }

            if (state.saved) {
                ConsentStatusObservable.setStatus(state.data.consentStatus)
                tryFinish()
                return
            }
        } else {
            if (state.data.consentStatus != ConsentStatus.UNKNOWN) {
                ConsentStatusObservable.setStatus(state.data.consentStatus)
                if (onComplete()) {
                    tryFinish()
                    return
                }
            }
        }

        setCurrentItem(state)
    }

    /**
     * Called when the user completes her choice.
     *
     * It is not called if this activity has been has been launched as a
     * settings activity.
     *
     * @return Returns `true` if the method has been succeded, `false`
     *         otherwise.
     */
    public abstract fun onComplete(): Boolean

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        public const val PROP_SETTINGS: String = AdsExt.PROP_SETTINGS
    }
}
