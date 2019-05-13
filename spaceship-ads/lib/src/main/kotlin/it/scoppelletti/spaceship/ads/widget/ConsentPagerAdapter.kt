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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.ads.widget

import android.content.Context
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.app.ConsentAgeFragment
import it.scoppelletti.spaceship.ads.app.ConsentPromptFragment
import it.scoppelletti.spaceship.ads.app.ConsentReminderFragment
import it.scoppelletti.spaceship.ads.app.ConsentPrivacyFragment
import it.scoppelletti.spaceship.ads.app.ConsentLoadFragment
import it.scoppelletti.spaceship.ads.app.ConsentUnderageFragment
import it.scoppelletti.spaceship.widget.FragmentPagerAdapterEx

/**
 * Adapter for the page fragments of the `AbstractConsentActivity`.
 *
 * @see   it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
 * @see   it.scoppelletti.spaceship.ads.app.ConsentLoadFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentAgeFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentUnderageFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentPromptFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentPrivacyFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentReminderFragment
 * @since 1.0.0
 *
 * @constructor             Constructor.
 * @param       context     Context.
 * @param       fragmentMgr Fragment Manager.
 */
@UiThread
public class ConsentPagerAdapter(
        private val context: Context,
        fragmentMgr: FragmentManager
) : FragmentPagerAdapterEx(fragmentMgr) {

    override fun getCount(): Int = 6

    override fun getItem(position: Int): Fragment =
            when (position) {
                ConsentLoadFragment.POS -> ConsentLoadFragment.newInstance()
                ConsentAgeFragment.POS -> ConsentAgeFragment.newInstance()
                ConsentUnderageFragment.POS ->
                    ConsentUnderageFragment.newInstance()
                ConsentPromptFragment.POS -> ConsentPromptFragment.newInstance()
                ConsentPrivacyFragment.POS ->
                    ConsentPrivacyFragment.newInstance()
                ConsentReminderFragment.POS ->
                    ConsentReminderFragment.newInstance()
                else -> throw IllegalArgumentException("Invalid position.")
            }

    override fun getPageTitle(position: Int): CharSequence? =
            context.getString(when (position) {
                ConsentAgeFragment.POS ->
                    R.string.it_scoppelletti_ads_lbl_underage
                ConsentUnderageFragment.POS ->
                    R.string.it_scoppelletti_ads_lbl_underage
                ConsentPrivacyFragment.POS ->
                    R.string.it_scoppelletti_ads_lbl_privacy
                ConsentReminderFragment.POS ->
                    R.string.it_scoppelletti_ads_lbl_reminder
                else -> R.string.app_name
            })
}
