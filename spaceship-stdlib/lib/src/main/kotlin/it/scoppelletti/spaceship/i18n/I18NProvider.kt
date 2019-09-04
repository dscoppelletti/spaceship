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

package it.scoppelletti.spaceship.i18n

import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
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
     * Returns the current `ZoneId`.
     *
     * @return The object.
     */
    fun currentZoneId(): ZoneId

    /**
     * Creates a date formatter.
     *
     * @return The new object.
     */
    fun createDateFormatter(): DateTimeFormatter

    /**
     * Creates the pattern for formatting dates.
     *
     * @return The value.
     */
    fun createDatePattern(): String
}
