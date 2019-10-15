/*
 * Copyright (C) 2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.types

import android.content.Context
import android.content.res.Resources
import android.text.format.DateFormat
import it.scoppelletti.spaceship.R
import it.scoppelletti.spaceship.i18n.I18NProvider

private const val TIME_SEP = ':'

/**
 * Implementation of `TimeConverter` interface for Android.
 *
 * @since 1.0.0
 *
 * @constructor
 * @param       secs         Whether the seconds field is enabled.
 * @param       context      Context.
 * @param       resources    Resources of this application.
 * @param       i18nProvider Interface `I18NProvider`.
 */
public class AndroidTimeConverter(
        private val secs: Boolean,
        private val context: Context,
        private val resources: Resources,
        i18nProvider: I18NProvider
) : AbstractTimeConverter(secs, i18nProvider) {

    override fun pattern(): String {
        val is24HourFormat: Boolean = is24HourFormat()

        return buildString {
            append(resources.getString(if (is24HourFormat)
                R.string.it_scoppelletti_field_hour24
                    else R.string.it_scoppelletti_field_hour12))
            append(TIME_SEP)
            append(resources.getString(R.string.it_scoppelletti_field_minute))

            if (secs) {
                append(TIME_SEP)
                append(resources.getString(
                        R.string.it_scoppelletti_field_second))
            }

            if (!is24HourFormat) {
                append(' ')
                append(resources.getString(R.string.it_scoppelletti_field_ampm))
            }
        }
    }

    override fun is24HourFormat(): Boolean =
            DateFormat.is24HourFormat(context)
}
