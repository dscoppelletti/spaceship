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
import androidx.annotation.StringRes
import it.scoppelletti.spaceship.R
import it.scoppelletti.spaceship.i18n.I18NProvider
import org.threeten.bp.temporal.ChronoField

private const val DATE_SEP = '/'

/**
 * Implementation of `DateConverter` interface for Android.
 *
 * @since 1.0.0
 *
 * @constructor
 * @param       context      Context.
 * @param       resources    Resources of this application.
 * @param       i18nProvider Interface `I18NProvider`.
 */
public class AndroidDateConverter(
        private val context: Context,
        private val resources: Resources,
        i18nProvider: I18NProvider
) : AbstractDateConverter(i18nProvider) {

    override fun pattern(): String {
        val order: Array<ChronoField> = getDateFormatOrder()

        return buildString {
            append(resources.getString(order[0].toStringId()))
            append(DATE_SEP)
            append(resources.getString(order[1].toStringId()))
            append(DATE_SEP)
            append(resources.getString(order[2].toStringId()))
        }
    }

    override fun getDateFormatOrder(): Array<ChronoField> =
            DateFormat.getDateFormatOrder(context)
                    .map { c ->
                        when (c.toLowerCase()) {
                            'y' -> ChronoField.YEAR
                            'm' -> ChronoField.MONTH_OF_YEAR
                            'd' -> ChronoField.DAY_OF_MONTH
                            else -> throw IllegalArgumentException(
                                    "Character placeholder $c not supported.")
                        }
                    }
                    .toTypedArray()
}

@StringRes
private fun ChronoField.toStringId(): Int =
        when (this) {
            ChronoField.YEAR -> R.string.it_scoppelletti_field_year
            ChronoField.MONTH_OF_YEAR -> R.string.it_scoppelletti_field_month
            ChronoField.DAY_OF_MONTH -> R.string.it_scoppelletti_field_day
            else -> throw IllegalArgumentException("Field $this not supported.")
        }
