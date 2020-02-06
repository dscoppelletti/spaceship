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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.types

import it.scoppelletti.spaceship.i18n.I18NProvider
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.format.SignStyle
import org.threeten.bp.temporal.ChronoField

private const val TIME_SEP = ':'

/**
 * Abstract mplementation of `TimeConverter` interface.
 *
 * @since 1.0.0
 *
 * @constructor
 * @param       secs         Whether the seconds field is enabled.
 * @param       i18nProvider Interface `I18NProvider`.
 */
public abstract class AbstractTimeConverter protected constructor(
        private val secs: Boolean,
        private val i18nProvider: I18NProvider
) : TimeConverter {

    private val formatter: DateTimeFormatter by lazy {
        createFormatter()
    }

    private val parser: DateTimeFormatter by lazy {
        createParser()
    }

    override fun format(value: LocalTime?): String? =
            value?.format(formatter)

    private fun createFormatter(): DateTimeFormatter {
        val is24HourFormat: Boolean = is24HourFormat()

        val builder = DateTimeFormatterBuilder()
                .padNext(2, ' ')
                .appendValue(if (is24HourFormat) ChronoField.HOUR_OF_DAY else
                    ChronoField.CLOCK_HOUR_OF_AMPM)
                .appendLiteral(TIME_SEP)
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)

        if (secs) {
            builder.appendLiteral(TIME_SEP)
                    .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        }

        if (!is24HourFormat) {
            builder.appendLiteral(' ')
                    .appendText(ChronoField.AMPM_OF_DAY)
        }

        return builder.toFormatter(i18nProvider.currentLocale())
    }

    override fun parse(text: String?): LocalTime? {
        if (text.isNullOrBlank()) {
            return null
        }

        val s = text.trim()
                .replace('.', TIME_SEP)
                .replace(',', TIME_SEP)
        return LocalTime.parse(s, parser)
    }

    private fun createParser(): DateTimeFormatter {
        val is24HourFormat: Boolean = is24HourFormat()

        val builder = DateTimeFormatterBuilder()
                .appendValue(if (is24HourFormat) ChronoField.HOUR_OF_DAY else
                    ChronoField.CLOCK_HOUR_OF_AMPM, 1, 2,
                        SignStyle.NOT_NEGATIVE)
                .appendLiteral(TIME_SEP)
                .appendValue(ChronoField.MINUTE_OF_HOUR, 1, 2,
                        SignStyle.NOT_NEGATIVE)

        if (secs) {
            builder.optionalStart()
            builder.appendLiteral(TIME_SEP)
            builder.appendValue(ChronoField.SECOND_OF_MINUTE, 1, 2,
                    SignStyle.NOT_NEGATIVE)
            builder.optionalEnd()
            builder.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
        }

        if (!is24HourFormat) {
            builder.optionalStart()
                    .appendLiteral(' ')
                    .optionalEnd()
                    .parseCaseInsensitive()
                    .appendText(ChronoField.AMPM_OF_DAY)
        }

        return builder.toFormatter(i18nProvider.currentLocale())
    }

    /**
     * Returns `true` if times should be formatted as 24 hour times, `false` if
     * times should be formatted as 12 hour (AM/PM) times.
     */
    protected abstract fun is24HourFormat(): Boolean
}
