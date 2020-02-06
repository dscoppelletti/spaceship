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

package it.scoppelletti.spaceship.types

import it.scoppelletti.spaceship.i18n.I18NProvider
import mu.KotlinLogging
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.ParsePosition

/**
 * Base implementation of `DecimalConverter` interface.
 *
 * @since 1.0.0
 *
 * @property format Provides the interface for formatting and parsing numbers.
 *
 * @constructor                     Constructor.
 * @param       fractionDigitsFixed Number of digits in the fraction portion of
 *                                  a number.
 * @param       groupingUsedFixed   Whether grouping will be used in this
 *                                  converter.
 * @param       i18nProvider        Provides I18N dependencies.
 */
public abstract class AbstractDecimalConverter protected constructor(
        private val fractionDigitsFixed: Int?,
        private val groupingUsedFixed: Boolean?,
        private val i18nProvider: I18NProvider
) : DecimalConverter {

    protected val format: NumberFormat by lazy {
        createFormat()
    }

    override val fractionDigits: Int
        get() = format.maximumFractionDigits

    override val roundingMode: RoundingMode
        get() = format.roundingMode

    override fun format(value: BigDecimal?): String? =
            if (value == null) null else format.format(value)

    @Throws(ParseException::class)
    override fun parse(text: String?): BigDecimal? {
        val x: BigDecimal?

        if (text.isNullOrBlank()) {
            return null
        }

        val trimmed = text.trim()
        val pos = ParsePosition(0)
        x = format.parseObject(trimmed, pos) as BigDecimal?
        if (x == null || pos.index < trimmed.length - 1) {
            throw ParseException("Illegal format.",
                    if (pos.errorIndex >= 0) pos.errorIndex else pos.index)
        }

        return try {
            x.setScale(format.maximumFractionDigits, format.roundingMode)
        } catch (ex: ArithmeticException) {
            throw ParseException("Illegal format.", pos.index)
                    .apply {
                        initCause(ex)
                    }
        }
    }

    private fun createFormat(): NumberFormat {
        val numberFmt: NumberFormat
        val decimalFmt: DecimalFormat

        numberFmt = NumberFormat.getNumberInstance(
                i18nProvider.currentLocale()).apply {
            if (fractionDigitsFixed == null) {
                maximumFractionDigits = defaultFractionDigits(this)
                minimumFractionDigits = maximumFractionDigits
            } else {
                minimumFractionDigits = fractionDigitsFixed
                maximumFractionDigits = fractionDigitsFixed
            }

            if (groupingUsedFixed != null) {
                isGroupingUsed = groupingUsedFixed
            }
        }

        try {
            decimalFmt = numberFmt as DecimalFormat
        } catch (ex: Exception) {
            logger.warn(ex) { """No DecimalFormat available for locale
                |${i18nProvider.currentLocale()}.""".trimMargin().joinLines() }
            return numberFmt
        }

        return decimalFmt.apply {
            isParseBigDecimal = true
        }
    }

    /**
     * Returns the default number of digits in the fraction portion of a number.
     *
     * @param  format Base format.
     * @return        Number of digits.
     */
    protected abstract fun defaultFractionDigits(format: NumberFormat): Int

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}
