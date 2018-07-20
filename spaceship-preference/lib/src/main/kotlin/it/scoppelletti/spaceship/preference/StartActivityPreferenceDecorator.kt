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

import android.support.annotation.StringRes
import android.support.v4.app.FragmentActivity
import android.support.v7.preference.Preference
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.app.ExceptionDialogFragment

/**
 * Decorates a `Preference` object so that it can start an activity by an
 * intent catching any exception.
 *
 * @property activity   The activity hosting the preferences.
 * @property preference The `Preference` object.
 * @property titleId    The title as a string resource ID.
 * @since               1.0.0
 *
 * @constructor Constructor.
 */
public class StartActivityPreferenceDecorator(
        private val activity: FragmentActivity,
        private val preference: Preference,
        @StringRes private val titleId: Int
) {
    init {
        preference.setOnPreferenceClickListener(::onClickListener)
    }

    private fun onClickListener(preference: Preference): Boolean {
        val err: ApplicationException

        if (preference.intent == null) {
            return false
        }

        try {
            preference.context.startActivity(preference.intent)
        } catch (ex: RuntimeException) {
            err = ApplicationException(
                    messageId = R.string.it_scoppelletti_err_startActivity,
                    titleId = titleId, cause = ex)
            ExceptionDialogFragment.show(activity, err)
        }

        return true
    }
}