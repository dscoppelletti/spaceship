/*
 * Copyright (C) 2008-2015 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

/**
 * Operations on strings.
 *
 * @since 1.0.0
 */
public object StringExt {

    /**
     * Empty string.
     */
    public const val EMPTY = ""
}

/**
 * Trims leading whitespace characters followed by `|` from every line of a
 * source string and removes the first and the last lines if they are blank
 * (notice difference blank vs empty). Then replace all `\n` characters by
 * whitespace ` `.
 *
 * Doesn't affect a line if it doesn't contain `|` except the first and the last
 * blank lines.
 *
 * Doesn't preserve the original line endings.
 *
 * @receiver Original string
 * @return   Resulting string.
 * @since    1.0.0
 */
public fun String.trimRaw() = this.trimMargin().replace('\n', ' ')