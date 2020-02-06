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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier",
        "unused")

package it.scoppelletti.spaceship.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Adapter for decimal numbers.
 *
 * @since 1.0.0
 *
 * @constructor                Constructor.
 * @param       fractionDigits Number of digits in the fraction portion of a
 *                             number.
 * @param       roundingMode   Rounding behavior.
 */
public class DecimalAdapter(
        private val fractionDigits: Int,
        private val roundingMode: RoundingMode
) : JsonAdapter<BigDecimal>() {

    override fun fromJson(reader: JsonReader): BigDecimal? {
        val value: String
        val x: BigDecimal

        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull()
        }

        value = reader.nextString()
        if (value.isBlank()) {
            return null
        }

        try {
            x = BigDecimal(value)
        } catch (ex: NumberFormatException) {
            throw JsonDataException(ex)
        }

        return try {
            x.setScale(fractionDigits, roundingMode)
        } catch (ex: ArithmeticException) {
            throw JsonDataException(ex)
        }
    }

    override fun toJson(writer: JsonWriter, value: BigDecimal?) {
        if (value == null) {
            writer.nullValue()
        } else {
            writer.value(value.toPlainString())
        }
    }
}
