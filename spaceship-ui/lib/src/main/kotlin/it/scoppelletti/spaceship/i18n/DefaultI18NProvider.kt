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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier")

package it.scoppelletti.spaceship.i18n

import android.content.Context
import android.content.res.Resources
import android.text.format.DateFormat
import androidx.annotation.StringRes
import it.scoppelletti.spaceship.R
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.TemporalField
import java.util.Locale
import javax.inject.Inject

private const val DATE_SEP = '/'

/**
 * Default implementation of the `I18NProvider` interface`.
 *
 * @since 1.0.0
 */
public class DefaultI18NProvider @Inject constructor(
        private val context: Context,
        private val resources: Resources
): I18NProvider {

    override fun currentLocale(): Locale = Locale.getDefault()

    override fun currentZoneId(): ZoneId = ZoneId.systemDefault()

    override fun createDateFormatter(): DateTimeFormatter {
        val order: CharArray

        order = DateFormat.getDateFormatOrder(context)

        return DateTimeFormatterBuilder()
                .appendValue(order[0].toTemporalField(), order[0].toWidth())
                .appendLiteral(DATE_SEP)
                .appendValue(order[1].toTemporalField(), order[1].toWidth())
                .appendLiteral(DATE_SEP)
                .appendValue(order[2].toTemporalField(), order[2].toWidth())
                .toFormatter(currentLocale())
    }

    override fun createDatePattern(): String {
        val order: CharArray

        order = DateFormat.getDateFormatOrder(context)

        return buildString {
            append(resources.getString(order[0].toStringId()))
            append(DATE_SEP)
            append(resources.getString(order[1].toStringId()))
            append(DATE_SEP)
            append(resources.getString(order[2].toStringId()))
        }
    }
}

private fun Char.toTemporalField(): TemporalField =
        when (this.toLowerCase()) {
            'y' -> ChronoField.YEAR
            'm' -> ChronoField.MONTH_OF_YEAR
            'd' -> ChronoField.DAY_OF_MONTH
            else -> throw IllegalArgumentException(
                    "Character placeholder $this not supported.")
        }

private fun Char.toWidth(): Int =
        if (this.toLowerCase() == 'y') 4 else 2

@StringRes
private fun Char.toStringId(): Int =
        when (this.toLowerCase()) {
            'y' -> R.string.it_scoppelletti_field_year
            'm' -> R.string.it_scoppelletti_field_month
            'd' -> R.string.it_scoppelletti_field_day
            else -> throw IllegalArgumentException(
                    "Character placeholder $this not supported.")
        }
