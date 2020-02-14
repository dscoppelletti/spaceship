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

@file:Suppress("JoinDeclarationAndAssignment", "RemoveRedundantQualifierName",
        "RedundantVisibilityModifier")

package it.scoppelletti.spaceship.preference

import android.content.Intent
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.uiComponent
import it.scoppelletti.spaceship.i18n.UIMessages

/**
 * Settings fragment for supporting [CustomPreferenceDialogFragment].
 *
 * @since 1.0.0
 */
@UiThread
public abstract class AbstractPreferenceFragment : PreferenceFragmentCompat() {

    private lateinit var uiMessages: UIMessages

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        uiMessages = requireActivity().uiComponent().uiMessages()
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        val fragment: CustomPreferenceDialogFragment
        val fragmentMgr: FragmentManager

        fragmentMgr = parentFragmentManager
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

    /**
     * Configures a `Preference` object so that it can start an activity by an
     * intent catching any exception.
     *
     * @receiver        The `Preference` object.
     * @param    config Intent configuration.
     */
    protected fun Preference.startActivityConfig(
            config: ((Intent) -> Unit)? = null
    ) {
        this.setOnPreferenceClickListener { pref ->
            onStartActivity(pref, config)
        }
    }

    /**
     * Starts an activity by a `Preference` object.
     *
     * @param preference The `Preference` object.
     * @param config     Intent configuration.
     */
    private fun onStartActivity(
            preference: Preference,
            config: ((Intent) -> Unit)?): Boolean
    {
        val intent: Intent
        val activity: AppCompatActivity
        val err: ApplicationException

        if (preference.intent == null) {
            return false
        }

        if (config == null) {
            intent = preference.intent
        } else {
            intent = Intent(preference.intent)
            config.invoke(intent)
        }

        activity = requireActivity() as AppCompatActivity
        try {
            activity.startActivity(intent)
        } catch (ex: RuntimeException) {
            err = ApplicationException(uiMessages.errorStartActivity(), ex)

            activity.showExceptionDialog(err) {
                title {
                    preference.title.toString()
                }
            }
        }

        return true
    }

    public companion object {

        /**
         * Tag.
         */
        public const val DIALOG_FRAGMENT_TAG =
                "android.support.v7.preference.PreferenceFragment.DIALOG"
        // - Support library 27.1.1
        // In the base class this constant is private.
    }
}
