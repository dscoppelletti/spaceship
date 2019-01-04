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

import android.content.Intent
import androidx.annotation.UiThread
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.applicationException

/**
 * Decorates a `Preference` object so that it can start an activity by an
 * intent catching any exception.
 *
 * @since 1.0.0
 *
 * @constructor            Constructor.
 * @param       activity   Activity hosting the preferences.
 * @param       preference The `Preference` object.
 * @param       config     Intent configuration.
 */
@UiThread
public class StartActivityPreferenceDecorator(
        private val activity: FragmentActivity,
        private val preference: Preference,
        private val config: ((Intent) -> Unit)? = null
) {

    init {
        preference.setOnPreferenceClickListener(::onClickListener)
    }

    private fun onClickListener(preference: Preference): Boolean {
        val intent: Intent
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

        try {
            preference.context.startActivity(intent)
        } catch (ex: RuntimeException) {
            err = applicationException {
                message(R.string.it_scoppelletti_err_startActivity)
                cause = ex
            }

            activity.showExceptionDialog(err) {
                title(preference.title.toString())
            }
        }

        return true
    }
}