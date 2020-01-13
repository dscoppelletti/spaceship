/*
 * Copyright (C) 2020 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle.java;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Operations of strings.
 */
final class StringExt {

    /**
     * Empty string.
     */
    static final String EMPTY = "";

    /**
     * Private constructor for static class.
     */
    private StringExt() {
    }

    /**
     * Converts a string to {@code }camelCase}.
     *
     * @param  value Original string.
     * @return       Resulting string.
     */
    @Nonnull
    static String toCamelCase(@Nullable String value) {
        int i, n;
        char c;
        boolean newWord;
        StringBuilder buf;

        if (value == null) {
            return StringExt.EMPTY;
        }

        n = value.length();
        newWord = true;
        buf = new StringBuilder(n);

        for (i = 0; i < n; i++) {
            c = value.charAt(i);
            if (Character.isWhitespace(c) || !Character.isLetterOrDigit(c)) {
                newWord = true;
            } else {
                if (newWord && buf.length() > 0) {
                    buf.append(Character.toUpperCase(c));
                } else {
                    buf.append(Character.toLowerCase(c));
                }

                newWord = false;
            }
        }

        return buf.toString();
    }
}
