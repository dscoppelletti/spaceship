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
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.format.DateTimeParseException
import org.threeten.bp.format.SignStyle
import org.threeten.bp.temporal.ChronoField

private const val DATE_SEP = '/'

/**
 * Abstract implementation of `DateConverter` interface.
 *
 * @since 1.0.0
 *
 * @constructor
 * @param       i18nProvider Provides I18N dependencies.
 */
public abstract class AbstractDateConverter protected constructor(
        private val i18nProvider: I18NProvider
) : DateConverter {

    private val formatter: DateTimeFormatter by lazy {
        createFormatter()
    }

    private val parser: DateTimeFormatter by lazy {
        createParser()
    }

    override fun format(value: LocalDate?): String? =
            value?.format(formatter)

    private fun createFormatter() : DateTimeFormatter {
        val order: Array<ChronoField> = getDateFormatOrder()

        return DateTimeFormatterBuilder()
                .appendValue(order[0], order[0].toMaxWidth())
                .appendLiteral(DATE_SEP)
                .appendValue(order[1], order[1].toMaxWidth())
                .appendLiteral(DATE_SEP)
                .appendValue(order[2], order[2].toMaxWidth())
                .toFormatter(i18nProvider.currentLocale())
    }

    @Throws(DateTimeParseException::class)
    override fun parse(text: String?): LocalDate? {
        if (text.isNullOrBlank()) {
            return null
        }

        val s = text.trim()
                .replace('-', DATE_SEP)
                .replace('.', DATE_SEP)
        return LocalDate.parse(s, parser)
    }

    private fun createParser() : DateTimeFormatter {
        val order: Array<ChronoField> = getDateFormatOrder()

        return DateTimeFormatterBuilder()
                .appendValue(order[0], order[0].toMinWidth(),
                        order[0].toMaxWidth(), SignStyle.NOT_NEGATIVE)
                .appendLiteral(DATE_SEP)
                .appendValue(order[1], order[1].toMinWidth(),
                        order[1].toMaxWidth(), SignStyle.NOT_NEGATIVE)
                .appendLiteral(DATE_SEP)
                .appendValue(order[2], order[2].toMinWidth(),
                        order[2].toMaxWidth(), SignStyle.NOT_NEGATIVE)
                .toFormatter(i18nProvider.currentLocale())
    }

    /**
     * Gets the current date format.
     *
     * @return The date format as an array of three `ChronoField` values.
     */
    protected abstract fun getDateFormatOrder(): Array<ChronoField>
}

private fun ChronoField.toMinWidth(): Int =
        if (this == ChronoField.YEAR) 4 else 1

private fun ChronoField.toMaxWidth(): Int =
        if (this == ChronoField.YEAR) 4 else 2
