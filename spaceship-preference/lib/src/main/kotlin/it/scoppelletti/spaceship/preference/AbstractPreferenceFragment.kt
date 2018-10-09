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

package it.scoppelletti.spaceship.preference

import androidx.annotation.UiThread
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

/**
 * Settings fragment for supporting [CustomPreferenceDialogFragment].
 *
 * @since 1.0.0
 *
 * @constructor Sole constructor.
 */
@UiThread
public abstract class AbstractPreferenceFragment : PreferenceFragmentCompat() {

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        val fragment: CustomPreferenceDialogFragment
        val fragmentMgr: FragmentManager

        fragmentMgr = requireFragmentManager()
        if (fragmentMgr.findFragmentByTag(AbstractPreferenceFragment
                        .DIALOG_FRAGMENT_TAG) != null) {
            return
        }

        if (preference is CustomDialogPreference) {
            fragment = CustomPreferenceDialogFragment.newInstance(
                    preference.key)
            fragment.setTargetFragment(this, 0)
            fragment.show(fragmentMgr,
                    AbstractPreferenceFragment.DIALOG_FRAGMENT_TAG)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    public companion object {

        /**
         * Tag.
         */
        public const val DIALOG_FRAGMENT_TAG: String =
                "android.support.v7.preference.PreferenceFragment.DIALOG"
        // - Support library 27.1.1
        // In the base class this constant is private.
    }
}