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

import it.scoppelletti.spaceship.types.joinLines

/**
 * Message specification that can be resolved by a `ResourceBundle` object.
 *
 * @since 1.0.0
 *
 * @property clazz Class to determine the base name of the `ResourceBundle`
 *                 object.
 * @property key   Key of the string resource in the `ResourceBundle` object.
 * @property args  Arguments for the message. The string resource must include
 *                 patterns used for inserted argument; these patterns are
 *                 interpretated by the JDK class `MessageFormat`.
 */
public data class ResourceBundleMessageSpec(
        public val clazz: Class<*>,
        public val key: String,
        public val args: Array<Any?> = arrayOf()
) : MessageSpec {

    override fun hashCode(): Int {
        var value = 17

        value += value * 37 + clazz.hashCode()
        value += value * 37 + key.hashCode()
        value += value * 37 + args.contentDeepHashCode()

        return value
    }

    override fun equals(other: Any?): Boolean =
            other is ResourceBundleMessageSpec &&
                    clazz == other.clazz &&
                    key == other.key &&
                    args contentDeepEquals other.args

    override fun toString(): String =
            """ResourceBundleMessageSpec(clazz=${clazz.canonicalName},key=$key,
            |args=${args.contentDeepToString()})""".trimMargin().joinLines()
}
