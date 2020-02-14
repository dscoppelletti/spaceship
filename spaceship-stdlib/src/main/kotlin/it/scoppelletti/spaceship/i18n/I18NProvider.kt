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

package it.scoppelletti.spaceship.i18n

import it.scoppelletti.spaceship.types.CurrencyConverter
import it.scoppelletti.spaceship.types.DateConverter
import it.scoppelletti.spaceship.types.DecimalConverter
import it.scoppelletti.spaceship.types.DefaultCurrencyConverter
import it.scoppelletti.spaceship.types.DefaultDecimalConverter
import it.scoppelletti.spaceship.types.TimeConverter
import org.threeten.bp.ZoneId
import java.util.Locale

/**
 * Provides I18N dependencies.
 *
 * @since 1.0.0
 */
public interface I18NProvider {

    /**
     * Returns the current `Locale`.
     *
     * @return The object.
     */
    fun currentLocale(): Locale

    /**
     * Returns the converter between decimal numbers and strings.
     *
     * @param  fractionDigits Number of digits in the fraction portion of a
     *                        number.
     * @param  groupingUsed   Whether grouping will be used in the converter.
     * @return                The object.
     */
    fun decimalConverter(
            fractionDigits: Int? = null,
            groupingUsed: Boolean? = null
    ): DecimalConverter = DefaultDecimalConverter(fractionDigits, groupingUsed,
            this)

    /**
     * Returns the converter between currency amounts and strings.
     *
     * @param  fractionDigits Number of digits in the fraction portion of a
     *                        number.
     * @param  groupingUsed   Whether grouping will be used in the converter.
     * @return                The object.
     */
    @Suppress("unused")
    fun currencyConverter(
            fractionDigits: Int? = null,
            groupingUsed: Boolean? = null
    ): CurrencyConverter = DefaultCurrencyConverter(fractionDigits,
            groupingUsed, this)

    /**
     * Returns the current `ZoneId`.
     *
     * @return The object.
     */
    fun currentZoneId(): ZoneId

    /**
     * Returns the converter between dates and strings.
     *
     * @return The object.
     */
    fun dateConverter(): DateConverter

    /**
     * Returns the converter between times and strings.
     *
     * @param  secs Whether the seconds field is enabled.
     * @return      The object.
     */
    fun timeConverter(secs: Boolean): TimeConverter
}
